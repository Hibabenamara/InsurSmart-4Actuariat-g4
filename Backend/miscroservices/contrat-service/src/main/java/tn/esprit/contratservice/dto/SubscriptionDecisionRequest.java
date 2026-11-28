package tn.esprit.contratservice.dto;

import jakarta.validation.constraints.NotNull;
import tn.esprit.contratservice.entity.SubscriptionStatus;

public record SubscriptionDecisionRequest(
        @NotNull(message = "decision est obligatoire (APPROVED ou REJECTED)") SubscriptionStatus decision,
        String reason
) {}