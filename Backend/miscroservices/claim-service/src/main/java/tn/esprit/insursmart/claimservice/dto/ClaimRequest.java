package tn.esprit.insursmart.claimservice.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClaimRequest(
        @NotNull(message = "contractId est obligatoire") Long contractId,
        @NotNull(message = "incidentDate est obligatoire")
        @PastOrPresent(message = "incidentDate ne peut pas etre dans le futur") LocalDate incidentDate,
        @NotBlank(message = "description est obligatoire")
        @Size(max = 2000) String description,
        @Size(max = 255) String incidentLocation,
        @NotNull(message = "estimatedDamage est obligatoire")
        @Positive(message = "estimatedDamage doit etre > 0")
        @Digits(integer = 10, fraction = 2) BigDecimal estimatedDamage
) {}
