package tn.esprit.contratservice.dto;

import java.math.BigDecimal;

public record GuaranteeResponse(Long id, String name, BigDecimal coverageLimit, BigDecimal deductible) {}