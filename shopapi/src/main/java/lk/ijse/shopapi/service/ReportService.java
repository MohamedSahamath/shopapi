package lk.ijse.shopapi.service;

import lk.ijse.shopapi.dto.response.*;

import java.util.List;

public interface ReportService {
    MonthlySummaryResponse getMonthlySummary(int year, int month);
    List<TopProductResponse> getTopProducts(int limit);
    List<ExpenseBreakdownResponse> getExpenseBreakdown(int year, int month);
    List<ProductResponse> getLowStockProducts();
    List<MonthlySummaryResponse> getMonthlyTrend();
}