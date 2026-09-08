package lk.ijse.shopapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProductRequest {

    @NotNull(message = "Category id is required")
    private Long categoryId;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 150)
    private String name;

    @NotBlank(message = "SKU is required")
    @Pattern(regexp = "^[A-Z0-9\\-]{3,50}$",
            message = "SKU must be uppercase letters, digits or hyphens")
    private String sku;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.01", message = "Selling price must be greater than 0")
    private BigDecimal sellingPrice;

    @NotNull(message = "Cost price is required")
    @DecimalMin(value = "0.01", message = "Cost price must be greater than 0")
    private BigDecimal costPrice;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity;

    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;
}