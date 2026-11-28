package tn.esprit.contratservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "guarantees")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Guarantee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal coverageLimit;   // plafond

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deductible;      // franchise

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "insurance_type_id")
    private InsuranceType insuranceType;
}