package lk.ijse.shopapi.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TopProductResponse {
    private String sku;
    private String productName;
    private Long unitsSold;
    private BigDecimal revenue;
    private BigDecimal profit;
}