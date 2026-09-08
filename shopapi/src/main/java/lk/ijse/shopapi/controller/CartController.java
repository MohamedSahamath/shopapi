package lk.ijse.shopapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.ijse.shopapi.dto.request.*;
import lk.ijse.shopapi.dto.response.*;
import lk.ijse.shopapi.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Cart", description = "Shopping cart operations")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart() {
        return ResponseEntity.ok(ApiResponse.success(
                "Cart retrieved", cartService.getMyCart()));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Item added to cart", cartService.addItem(request)));
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody QuantityUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Quantity updated", cartService.updateQuantity(itemId, request)));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Item removed", cartService.removeItem(itemId)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}