package tn.esprit.contratservice.dto;

import tn.esprit.contratservice.entity.InsuranceCode;
import java.math.BigDecimal;
import java.util.List;

public record InsuranceTypeResponse(Long id, InsuranceCode code, String name, String description,
                                    BigDecimal basePremium, List<GuaranteeResponse> guarantees) {}