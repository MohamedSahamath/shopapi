package lk.ijse.shopapi.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String sku;
    private String description;
    private BigDecimal sellingPrice;
    private BigDecimal costPrice;
    private Integer stockQuantity;
    private Integer reorderLevel;
    private Boolean active;
    private Long categoryId;
    private String categoryName;
    private Boolean lowStock;
}