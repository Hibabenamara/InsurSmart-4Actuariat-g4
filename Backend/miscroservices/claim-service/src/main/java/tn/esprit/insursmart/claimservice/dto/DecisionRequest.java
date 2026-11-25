package tn.esprit.insursmart.claimservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tn.esprit.insursmart.claimservice.entity.ClaimStatus;

/** decision : APPROVED ou REJECTED. reason obligatoire en cas de rejet. */
public record DecisionRequest(
        @NotNull(message = "decision est obligatoire") ClaimStatus decision,
        @Size(max = 1000) String reason
) {}
