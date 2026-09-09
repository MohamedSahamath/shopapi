package lk.ijse.shopapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.ijse.shopapi.dto.request.*;
import lk.ijse.shopapi.dto.response.*;
import lk.ijse.shopapi.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
@Tag(name = "Expenses", description = "Operating expense management")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> findAll(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(
                "Expenses retrieved", expenseService.findAll(from, to)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Expense retrieved", expenseService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> create(
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expense recorded",
                        expenseService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> update(
            @PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Expense updated", expenseService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<ExpenseCategoryResponse>>> findAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(
                "Categories retrieved", expenseService.findAllCategories()));
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<ExpenseCategoryResponse>> createCategory(
            @Valid @RequestBody ExpenseCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created",
                        expenseService.createCategory(request)));
    }
}