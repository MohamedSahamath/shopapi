package lk.ijse.shopapi.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExpenseResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String description;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String recordedBy;
}