package lk.ijse.shopapi.service.impl;

import lk.ijse.shopapi.dto.request.PaymentRequest;
import lk.ijse.shopapi.dto.response.PaymentResponse;
import lk.ijse.shopapi.entity.*;
import lk.ijse.shopapi.exception.*;
import lk.ijse.shopapi.repository.*;
import lk.ijse.shopapi.service.PaymentService;
import lk.ijse.shopapi.util.SecurityUtil;
import lk.ijse.shopapi.util.enums.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public PaymentResponse pay(PaymentRequest request) {

        Customer customer = securityUtil.getCurrentCustomer();

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException("This order does not belong to you");
        }

        if (paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new DuplicateResourceException(
                    "This order has already been paid");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot pay for a cancelled order");
        }

        Payment payment = Payment.builder()
                .order(order)
                .transactionReference(generateReference())
                .amount(order.getTotal())
                .method(request.getMethod())
                .status(request.getMethod() == PaymentMethod.CARD
                        ? PaymentStatus.PAID
                        : PaymentStatus.PENDING)
                .cardLastFour(request.getMethod() == PaymentMethod.CARD
                        ? request.getCardLastFour()
                        : null)
                .paidAt(request.getMethod() == PaymentMethod.CARD
                        ? LocalDateTime.now()
                        : null)
                .build();

        payment = paymentRepository.save(payment);

        if (payment.getStatus() == PaymentStatus.PAID) {
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
        }

        log.info("Payment {} recorded for order {} - LKR {}",
                payment.getTransactionReference(),
                order.getOrderNumber(), payment.getAmount());

        return toResponse(payment,
                payment.getStatus() == PaymentStatus.PAID
                        ? "Payment successful. Your bill has been paid."
                        : "Order confirmed. Pay on delivery.");
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse findByOrder(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No payment found for order id: " + orderId));
        return toResponse(payment, null);
    }

    private String generateReference() {
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "TXN-" + date + "-" + String.format("%03d", new Random().nextInt(1000));
    }

    private PaymentResponse toResponse(Payment p, String message) {
        return PaymentResponse.builder()
                .id(p.getId())
                .orderNumber(p.getOrder().getOrderNumber())
                .transactionReference(p.getTransactionReference())
                .amount(p.getAmount())
                .method(p.getMethod().name())
                .status(p.getStatus().name())
                .cardLastFour(p.getCardLastFour())
                .paidAt(p.getPaidAt())
                .message(message)
                .build();
    }
}