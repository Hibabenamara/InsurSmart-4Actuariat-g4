package tn.esprit.contratservice.dto;

import tn.esprit.contratservice.entity.ContractStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ContractResponse(Long id, String userId, String policyNumber, LocalDate startDate,
                               LocalDate endDate, BigDecimal premium, ContractStatus status,
                               List<GuaranteeResponse> guarantees) {}