package lk.ijse.shopapi.service;

import lk.ijse.shopapi.dto.request.*;
import lk.ijse.shopapi.dto.response.CartResponse;

public interface CartService {
    CartResponse getMyCart();
    CartResponse addItem(CartItemRequest request);
    CartResponse updateQuantity(Long itemId, QuantityUpdateRequest request);
    CartResponse removeItem(Long itemId);
    void clearCart();
}