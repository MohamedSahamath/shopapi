package lk.ijse.shopapi.service.impl;

import lk.ijse.shopapi.dto.request.*;
import lk.ijse.shopapi.dto.response.*;
import lk.ijse.shopapi.entity.*;
import lk.ijse.shopapi.exception.*;
import lk.ijse.shopapi.repository.*;
import lk.ijse.shopapi.service.OrderService;
import lk.ijse.shopapi.util.SecurityUtil;
import lk.ijse.shopapi.util.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal DELIVERY_FEE = new BigDecimal("350.00");
    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("5000.00");

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CustomerAddressRepository addressRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public OrderResponse checkout(CheckoutRequest request) {

        Customer customer = securityUtil.getCurrentCustomer();

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new BadRequestException("Your cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty");
        }

        CustomerAddress address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException("This address does not belong to you");
        }

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .customer(customer)
                .address(address)
                .status(OrderStatus.PENDING)
                .subtotal(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (cartItem.getQuantity() > product.getStockQuantity()) {
                throw new BadRequestException(
                        "Insufficient stock for " + product.getName()
                                + ". Available: " + product.getStockQuantity());
            }

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(product.getSellingPrice())
                    .costPriceAtSale(product.getCostPrice())
                    .build();

            orderItems.add(orderItem);

            subtotal = subtotal.add(product.getSellingPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        BigDecimal deliveryFee = subtotal.compareTo(FREE_DELIVERY_THRESHOLD) >= 0
                ? BigDecimal.ZERO
                : DELIVERY_FEE;

        order.setSubtotal(subtotal);
        order.setDeliveryFee(deliveryFee);
        order.setTotal(subtotal.add(deliveryFee));
        order.setItems(orderItems);

        Order saved = orderRepository.save(order);

        cartItemRepository.deleteByCartId(cart.getId());

        log.info("Order {} placed by {} for LKR {}",
                saved.getOrderNumber(), customer.getFullName(), saved.getTotal());

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findMyOrders() {
        Customer customer = securityUtil.getCurrentCustomer();
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customer.getId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        return toResponse(orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id)));
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id));

        order.setStatus(request.getStatus());
        log.info("Order {} status changed to {}",
                order.getOrderNumber(), request.getStatus());

        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse cancel(Long id) {
        Customer customer = securityUtil.getCurrentCustomer();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException("This order does not belong to you");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException(
                    "Only pending orders can be cancelled. Current status: "
                            + order.getStatus());
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        log.info("Order {} cancelled, stock restored", order.getOrderNumber());

        return toResponse(orderRepository.save(order));
    }

    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", new Random().nextInt(10000));
        String number = "ORD-" + date + "-" + random;

        while (orderRepository.existsByOrderNumber(number)) {
            random = String.format("%04d", new Random().nextInt(10000));
            number = "ORD-" + date + "-" + random;
        }
        return number;
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getName())
                        .sku(i.getProduct().getSku())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .lineTotal(i.getUnitPrice()
                                .multiply(BigDecimal.valueOf(i.getQuantity())))
                        .build())
                .toList();

        CustomerAddress a = order.getAddress();
        String fullAddress = a.getLine1()
                + (a.getLine2() != null ? ", " + a.getLine2() : "")
                + ", " + a.getCity();

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomer().getFullName())
                .deliveryAddress(fullAddress)
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name())
                .subtotal(order.getSubtotal())
                .deliveryFee(order.getDeliveryFee())
                .total(order.getTotal())
                .items(items)
                .build();
    }
}