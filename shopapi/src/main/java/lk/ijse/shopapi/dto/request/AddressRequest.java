package lk.ijse.shopapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AddressRequest {

    @NotBlank(message = "Address line 1 is required")
    @Size(max = 150)
    private String line1;

    @Size(max = 150)
    private String line2;

    @NotBlank(message = "City is required")
    @Size(max = 60)
    private String city;

    @Pattern(regexp = "^[0-9]{5}$", message = "Postal code must be 5 digits")
    private String postalCode;

    private Boolean isDefault;
}