package lk.ijse.shopapi.service.impl;

import lk.ijse.shopapi.dto.request.*;
import lk.ijse.shopapi.dto.response.*;
import lk.ijse.shopapi.entity.*;
import lk.ijse.shopapi.exception.*;
import lk.ijse.shopapi.repository.*;
import lk.ijse.shopapi.service.CartService;
import lk.ijse.shopapi.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getMyCart() {
        return toResponse(getOrCreateCart());
    }

    @Override
    @Transactional
    public CartResponse addItem(CartItemRequest request) {
        Cart cart = getOrCreateCart();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new BadRequestException("This product is no longer available");
        }

        CartItem existing = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        int newQuantity = (existing == null)
                ? request.getQuantity()
                : existing.getQuantity() + request.getQuantity();

        if (newQuantity > product.getStockQuantity()) {
            throw new BadRequestException(
                    "Only " + product.getStockQuantity() + " units available for "
                            + product.getName());
        }

        if (existing == null) {
            cartItemRepository.save(CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(newQuantity)
                    .build());
        } else {
            existing.setQuantity(newQuantity);
            cartItemRepository.save(existing);
        }

        log.info("Cart updated for {}: {} x{}",
                securityUtil.getCurrentEmail(), product.getSku(), newQuantity);

        return toResponse(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public CartResponse updateQuantity(Long itemId, QuantityUpdateRequest request) {
        CartItem item = getOwnedItemOrThrow(itemId);

        if (request.getQuantity() > item.getProduct().getStockQuantity()) {
            throw new BadRequestException(
                    "Only " + item.getProduct().getStockQuantity() + " units available");
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return toResponse(item.getCart());
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long itemId) {
        CartItem item = getOwnedItemOrThrow(itemId);
        Cart cart = item.getCart();
        cartItemRepository.delete(item);
        return toResponse(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public void clearCart() {
        Cart cart = getOrCreateCart();
        cartItemRepository.deleteByCartId(cart.getId());
    }

    private Cart getOrCreateCart() {
        Customer customer = securityUtil.getCurrentCustomer();
        return cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().customer(customer).build()));
    }

    private CartItem getOwnedItemOrThrow(Long itemId) {
        Customer customer = securityUtil.getCurrentCustomer();
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found with id: " + itemId));

        if (!item.getCart().getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException("This cart item does not belong to you");
        }
        return item;
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(i -> CartItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getName())
                        .sku(i.getProduct().getSku())
                        .unitPrice(i.getProduct().getSellingPrice())
                        .quantity(i.getQuantity())
                        .lineTotal(i.getProduct().getSellingPrice()
                                .multiply(BigDecimal.valueOf(i.getQuantity())))
                        .availableStock(i.getProduct().getStockQuantity())
                        .build())
                .toList();

        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .totalItems(items.size())
                .subtotal(subtotal)
                .build();
    }
}