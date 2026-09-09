package lk.ijse.shopapi.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExpenseBreakdownResponse {
    private String categoryName;
    private BigDecimal totalAmount;
    private Long entryCount;
    private BigDecimal percentageOfTotal;
}