package tn.esprit.contratservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record GuaranteeRequest(
        @NotBlank(message = "name est obligatoire") String name,
        @NotNull @Positive(message = "coverageLimit doit etre > 0") BigDecimal coverageLimit,
        @NotNull @PositiveOrZero(message = "deductible doit etre >= 0") BigDecimal deductible
) {}