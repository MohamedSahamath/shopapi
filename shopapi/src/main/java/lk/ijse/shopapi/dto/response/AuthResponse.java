package lk.ijse.shopapi.dto.response;

import lombok.*;

import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String tokenType;
    private String email;
    private String fullName;
    private Set<String> roles;
}