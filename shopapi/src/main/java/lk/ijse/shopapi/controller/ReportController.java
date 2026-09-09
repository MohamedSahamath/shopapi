package lk.ijse.shopapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lk.ijse.shopapi.dto.response.*;
import lk.ijse.shopapi.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Reports", description = "Financial reports (ADMIN only)")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly-summary")
    @Operation(summary = "Income, expenses and profit for a month")
    public ResponseEntity<ApiResponse<MonthlySummaryResponse>> monthlySummary(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        LocalDate now = LocalDate.now();
        int y = year == null ? now.getYear() : year;
        int m = month == null ? now.getMonthValue() : month;

        return ResponseEntity.ok(ApiResponse.success(
                "Monthly summary generated",
                reportService.getMonthlySummary(y, m)));
    }

    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse<List<TopProductResponse>>> topProducts(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.success(
                "Top products retrieved", reportService.getTopProducts(limit)));
    }

    @GetMapping("/expense-breakdown")
    public ResponseEntity<ApiResponse<List<ExpenseBreakdownResponse>>> expenseBreakdown(
            @RequestParam Integer year, @RequestParam Integer month) {
        return ResponseEntity.ok(ApiResponse.success(
                "Expense breakdown retrieved",
                reportService.getExpenseBreakdown(year, month)));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> lowStock() {
        return ResponseEntity.ok(ApiResponse.success(
                "Low stock products retrieved", reportService.getLowStockProducts()));
    }

    @GetMapping("/monthly-trend")
    @Operation(summary = "Last 12 months of performance")
    public ResponseEntity<ApiResponse<List<MonthlySummaryResponse>>> monthlyTrend() {
        return ResponseEntity.ok(ApiResponse.success(
                "Monthly trend retrieved", reportService.getMonthlyTrend()));
    }
}