package lk.ijse.shopapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 80, message = "Name must be 2-80 characters")
    private String name;

    @Size(max = 250, message = "Description cannot exceed 250 characters")
    private String description;
}