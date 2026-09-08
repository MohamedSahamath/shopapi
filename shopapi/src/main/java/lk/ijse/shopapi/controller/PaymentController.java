package lk.ijse.shopapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.ijse.shopapi.dto.request.PaymentRequest;
import lk.ijse.shopapi.dto.response.*;
import lk.ijse.shopapi.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Simulated payment processing")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Pay for an order (simulated gateway)")
    public ResponseEntity<ApiResponse<PaymentResponse>> pay(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment processed",
                        paymentService.pay(request)));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> findByOrder(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Payment retrieved", paymentService.findByOrder(orderId)));
    }
}