package tn.esprit.insursmart.client_service.dto;

import lombok.*;
import tn.esprit.insursmart.client_service.entity.ClientStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {
    private Long id;
    private String keycloakUserId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private ClientStatus status;
    private LocalDateTime createdAt;
    private AddressDto address;
    private PreferencesDto preferences;
}