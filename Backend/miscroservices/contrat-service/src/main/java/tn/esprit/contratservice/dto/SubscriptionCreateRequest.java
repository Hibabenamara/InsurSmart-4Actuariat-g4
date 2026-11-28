package tn.esprit.contratservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record SubscriptionCreateRequest(
        @NotNull(message = "insuranceTypeId est obligatoire") Long insuranceTypeId,
        @NotNull @FutureOrPresent(message = "requestedStart ne peut pas etre dans le passe") LocalDate requestedStart
) {}