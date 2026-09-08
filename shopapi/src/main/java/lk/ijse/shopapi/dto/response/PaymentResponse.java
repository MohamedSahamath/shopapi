package lk.ijse.shopapi.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentResponse {
    private Long id;
    private String orderNumber;
    private String transactionReference;
    private BigDecimal amount;
    private String method;
    private String status;
    private String cardLastFour;
    private LocalDateTime paidAt;
    private String message;
}