package lk.ijse.shopapi.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AddressResponse {
    private Long id;
    private String line1;
    private String line2;
    private String city;
    private String postalCode;
    private Boolean isDefault;
}