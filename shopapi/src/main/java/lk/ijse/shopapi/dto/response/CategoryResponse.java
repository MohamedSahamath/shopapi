package lk.ijse.shopapi.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Long productCount;
}