package tn.esprit.contratservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "insurance_types")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InsuranceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private InsuranceCode code;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basePremium;

    @OneToMany(mappedBy = "insuranceType", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Guarantee> guarantees = new ArrayList<>();

    public void addGuarantee(Guarantee guarantee) {
        guarantees.add(guarantee);
        guarantee.setInsuranceType(this);
    }
}