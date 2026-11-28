package tn.esprit.contratservice.dto;

import jakarta.validation.constraints.*;
import tn.esprit.contratservice.entity.InsuranceCode;
import java.math.BigDecimal;

public record InsuranceTypeRequest(
        @NotNull(message = "code est obligatoire") InsuranceCode code,
        @NotBlank(message = "name est obligatoire") String name,
        @Size(max = 1000) String description,
        @NotNull @Positive(message = "basePremium doit etre > 0") BigDecimal basePremium
) {}