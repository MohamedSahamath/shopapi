package lk.ijse.shopapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SupplierRequest {

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 120)
    private String companyName;

    @Size(max = 100)
    private String contactPerson;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(0|94|\\+94)?[0-9]{9}$", message = "Invalid phone number")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 200)
    private String address;
}