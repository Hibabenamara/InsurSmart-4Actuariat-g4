package tn.esprit.contratservice.dto;

import jakarta.validation.constraints.NotNull;
import tn.esprit.contratservice.entity.ContractStatus;

public record ContractStatusRequest(@NotNull(message = "status est obligatoire") ContractStatus status) {}