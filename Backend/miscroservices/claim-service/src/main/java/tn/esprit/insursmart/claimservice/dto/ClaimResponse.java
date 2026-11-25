package tn.esprit.insursmart.claimservice.dto;

import tn.esprit.insursmart.claimservice.entity.Claim;
import tn.esprit.insursmart.claimservice.entity.ClaimStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ClaimResponse(Long id, String userId, Long contractId, LocalDate incidentDate,
                            String description, String incidentLocation, BigDecimal estimatedDamage,
                            ClaimStatus status, String decisionReason, LocalDateTime createdAt,
                            List<DocumentResponse> documents) {
    public static ClaimResponse from(Claim c) {
        return new ClaimResponse(c.getId(), c.getUserId(), c.getContractId(), c.getIncidentDate(),
                c.getDescription(), c.getIncidentLocation(), c.getEstimatedDamage(),
                c.getStatus(), c.getDecisionReason(), c.getCreatedAt(),
                c.getDocuments().stream().map(DocumentResponse::from).toList());
    }
}
