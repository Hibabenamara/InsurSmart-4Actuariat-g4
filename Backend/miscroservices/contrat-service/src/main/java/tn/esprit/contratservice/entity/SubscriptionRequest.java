package tn.esprit.contratservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubscriptionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;          // identité Keycloak, jamais une clé étrangère

    @Column(nullable = false)
    private LocalDate requestedStart;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.SUBMITTED;

    @Column(length = 1000)
    private String decisionReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "insurance_type_id")
    private InsuranceType insuranceType;

    @OneToOne
    @JoinColumn(name = "contract_id", unique = true)
    private Contract contract;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}