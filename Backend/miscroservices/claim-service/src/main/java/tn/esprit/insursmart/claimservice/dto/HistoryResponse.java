package tn.esprit.insursmart.claimservice.dto;

import tn.esprit.insursmart.claimservice.entity.ClaimStatus;
import tn.esprit.insursmart.claimservice.entity.ClaimStatusHistory;

import java.time.LocalDateTime;

public record HistoryResponse(Long id, ClaimStatus oldStatus, ClaimStatus newStatus,
                              String changedBy, LocalDateTime changedAt, String comment) {
    public static HistoryResponse from(ClaimStatusHistory h) {
        return new HistoryResponse(h.getId(), h.getOldStatus(), h.getNewStatus(),
                h.getChangedBy(), h.getChangedAt(), h.getComment());
    }
}
