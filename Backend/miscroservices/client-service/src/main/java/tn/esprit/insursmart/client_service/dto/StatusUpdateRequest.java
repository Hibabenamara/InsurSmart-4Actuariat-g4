package tn.esprit.insursmart.client_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import tn.esprit.insursmart.client_service.entity.ClientStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusUpdateRequest {
    @NotNull(message = "Le statut est obligatoire")
    private ClientStatus status;
}