package lk.ijse.shopapi.dto.request;

import jakarta.validation.constraints.*;
import lk.ijse.shopapi.util.enums.PaymentMethod;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "Order id is required")
    private Long orderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    @Pattern(regexp = "^[0-9]{4}$", message = "Card last four must be 4 digits")
    private String cardLastFour;
}