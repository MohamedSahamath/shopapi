package lk.ijse.shopapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.ijse.shopapi.dto.request.FeedbackRequest;
import lk.ijse.shopapi.dto.response.*;
import lk.ijse.shopapi.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "Customer product feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "Read feedback for a product (public)")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> findByProduct(
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Feedback retrieved", feedbackService.findByProduct(productId)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Read all feedback (MANAGER/ADMIN)")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(
                "All feedback retrieved", feedbackService.findAll()));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> create(
            @Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Feedback submitted",
                        feedbackService.create(request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        feedbackService.delete(id);
        return ResponseEntity.noContent().build();
    }
}