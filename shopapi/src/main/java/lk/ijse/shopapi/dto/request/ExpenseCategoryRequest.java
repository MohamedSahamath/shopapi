package lk.ijse.shopapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ExpenseCategoryRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 60)
    private String name;

    @Size(max = 200)
    private String description;
}