package lk.ijse.shopapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.ijse.shopapi.dto.request.*;
import lk.ijse.shopapi.dto.response.*;
import lk.ijse.shopapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog management")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List active products (public)")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findAll(
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        return ResponseEntity.ok(ApiResponse.success(
                "Products retrieved", productService.findAll(includeInactive)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name or category")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Search completed", productService.search(name, categoryId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Product retrieved", productService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Create a product (MANAGER/ADMIN)")
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created",
                        productService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Full update - all fields required")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Product updated", productService.update(id, request)));
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Partial update - stock quantity only")
    public ResponseEntity<ApiResponse<ProductResponse>> updateStock(
            @PathVariable Long id, @Valid @RequestBody StockUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Stock updated", productService.updateStock(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}