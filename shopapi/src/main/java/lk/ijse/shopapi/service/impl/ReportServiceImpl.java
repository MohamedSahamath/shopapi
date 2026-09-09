package lk.ijse.shopapi.service.impl;

import lk.ijse.shopapi.dto.response.*;
import lk.ijse.shopapi.entity.Product;
import lk.ijse.shopapi.repository.*;
import lk.ijse.shopapi.repository.projection.*;
import lk.ijse.shopapi.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;
    private final ExpenseRepository expenseRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public MonthlySummaryResponse getMonthlySummary(int year, int month) {

        RevenueProjection rev = orderRepository.findRevenueForMonth(year, month);

        BigDecimal revenue = safe(rev == null ? null : rev.getRevenue());
        BigDecimal cogs = safe(rev == null ? null : rev.getCogs());
        long orderCount = (rev == null || rev.getOrderCount() == null)
                ? 0L : rev.getOrderCount();

        BigDecimal expenses = safe(
                expenseRepository.findTotalExpensesForMonth(year, month));

        BigDecimal grossProfit = revenue.subtract(cogs);
        BigDecimal netProfit = grossProfit.subtract(expenses);

        BigDecimal avgOrderValue = orderCount == 0
                ? BigDecimal.ZERO
                : revenue.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP);

        log.info("Report {}-{}: revenue={} cogs={} expenses={} net={}",
                year, month, revenue, cogs, expenses, netProfit);

        return MonthlySummaryResponse.builder()
                .year(year)
                .month(month)
                .periodLabel(Month.of(month)
                        .getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year)
                .revenue(revenue)
                .costOfGoodsSold(cogs)
                .grossProfit(grossProfit)
                .totalExpenses(expenses)
                .netProfit(netProfit)
                .grossMarginPercent(percentage(grossProfit, revenue))
                .netMarginPercent(percentage(netProfit, revenue))
                .orderCount(orderCount)
                .averageOrderValue(avgOrderValue)
                .expenseBreakdown(getExpenseBreakdown(year, month))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopProductResponse> getTopProducts(int limit) {
        return orderRepository.findTopSellingProducts(limit).stream()
                .map(p -> TopProductResponse.builder()
                        .sku(p.getSku())
                        .productName(p.getProductName())
                        .unitsSold(p.getUnitsSold())
                        .revenue(safe(p.getRevenue()))
                        .profit(safe(p.getProfit()))
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseBreakdownResponse> getExpenseBreakdown(int year, int month) {

        List<ExpenseBreakdownProjection> rows =
                expenseRepository.findExpenseBreakdownForMonth(year, month);

        BigDecimal total = rows.stream()
                .map(r -> safe(r.getTotalAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return rows.stream()
                .map(r -> ExpenseBreakdownResponse.builder()
                        .categoryName(r.getCategoryName())
                        .totalAmount(safe(r.getTotalAmount()))
                        .entryCount(r.getEntryCount())
                        .percentageOfTotal(percentage(safe(r.getTotalAmount()), total))
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
                .map(this::toProductResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlySummaryResponse> getMonthlyTrend() {
        List<MonthlyTrendProjection> rows = orderRepository.findMonthlyTrend();
        List<MonthlySummaryResponse> result = new ArrayList<>();

        for (MonthlyTrendProjection row : rows) {
            String[] parts = row.getPeriod().split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            result.add(getMonthlySummary(year, month));
        }
        return result;
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal percentage(BigDecimal part, BigDecimal whole) {
        if (whole == null || whole.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return part.multiply(BigDecimal.valueOf(100))
                .divide(whole, 2, RoundingMode.HALF_UP);
    }

    private ProductResponse toProductResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .sku(p.getSku())
                .sellingPrice(p.getSellingPrice())
                .costPrice(p.getCostPrice())
                .stockQuantity(p.getStockQuantity())
                .reorderLevel(p.getReorderLevel())
                .active(p.getActive())
                .categoryId(p.getCategory().getId())
                .categoryName(p.getCategory().getName())
                .lowStock(true)
                .build();
    }
}