package lk.ijse.shopapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Full name must be 3-100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "NIC is required")
    @Pattern(regexp = "^([0-9]{9}[vVxX]|[0-9]{12})$",
            message = "NIC must be 9 digits followed by V, or 12 digits")
    private String nic;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(0|94|\\+94)?[0-9]{9}$",
            message = "Phone number format is invalid")
    private String phone;
}