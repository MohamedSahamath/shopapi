package lk.ijse.shopapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lk.ijse.shopapi.util.enums.OrderStatus;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrderStatusRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}