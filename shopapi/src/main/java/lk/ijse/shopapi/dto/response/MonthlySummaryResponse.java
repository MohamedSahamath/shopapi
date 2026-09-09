package lk.ijse.shopapi.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MonthlySummaryResponse {

    private Integer year;
    private Integer month;
    private String periodLabel;

    private BigDecimal revenue;
    private BigDecimal costOfGoodsSold;
    private BigDecimal grossProfit;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;

    private BigDecimal grossMarginPercent;
    private BigDecimal netMarginPercent;

    private Long orderCount;
    private BigDecimal averageOrderValue;

    private List<ExpenseBreakdownResponse> expenseBreakdown;
}