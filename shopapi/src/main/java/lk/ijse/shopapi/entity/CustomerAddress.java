package lk.ijse.shopapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_addresses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "line1", nullable = false, length = 150)
    private String line1;

    @Column(name = "line2", length = 150)
    private String line2;

    @Column(nullable = false, length = 60)
    private String city;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;
}