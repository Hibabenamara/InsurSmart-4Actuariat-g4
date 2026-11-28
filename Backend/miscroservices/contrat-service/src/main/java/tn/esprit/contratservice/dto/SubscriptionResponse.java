package tn.esprit.contratservice.dto;

import tn.esprit.contratservice.entity.SubscriptionStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SubscriptionResponse(Long id, String userId, Long insuranceTypeId, String insuranceTypeName,
                                   LocalDate requestedStart, SubscriptionStatus status, String decisionReason,
                                   LocalDateTime createdAt, Long contractId) {}