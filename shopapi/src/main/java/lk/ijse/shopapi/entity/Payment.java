package lk.ijse.shopapi.entity;

import jakarta.persistence.*;
import lk.ijse.shopapi.util.enums.PaymentMethod;
import lk.ijse.shopapi.util.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "transaction_reference", nullable = false, unique = true, length = 50)
    private String transactionReference;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "card_last_four", length = 4)
    private String cardLastFour;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}