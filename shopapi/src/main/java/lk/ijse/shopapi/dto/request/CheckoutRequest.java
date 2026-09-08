package lk.ijse.shopapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CheckoutRequest {

    @NotNull(message = "Delivery address id is required")
    private Long addressId;
}