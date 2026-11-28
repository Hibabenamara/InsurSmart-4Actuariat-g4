package tn.esprit.insursmart.client_service.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.insursmart.client_service.dto.*;
import tn.esprit.insursmart.client_service.entity.*;

@Component
public class ClientMapper {

    public Client toEntity(ClientRequest req) {
        Client client = Client.builder()
                .keycloakUserId(req.getKeycloakUserId())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .birthDate(req.getBirthDate())
                .build();

        if (req.getAddress() != null) {
            Address address = Address.builder()
                    .street(req.getAddress().getStreet())
                    .city(req.getAddress().getCity())
                    .postalCode(req.getAddress().getPostalCode())
                    .country(req.getAddress().getCountry())
                    .client(client)
                    .build();
            client.setAddress(address);
        }

        CustomerPreferences prefs = CustomerPreferences.builder()
                .notificationChannel(req.getPreferences() != null && req.getPreferences().getNotificationChannel() != null
                        ? req.getPreferences().getNotificationChannel()
                        : NotificationChannel.EMAIL)
                .language(req.getPreferences() != null && req.getPreferences().getLanguage() != null
                        ? req.getPreferences().getLanguage()
                        : "fr")
                .client(client)
                .build();
        client.setPreferences(prefs);

        return client;
    }

    public ClientResponse toResponse(Client client) {
        AddressDto addressDto = null;
        if (client.getAddress() != null) {
            addressDto = AddressDto.builder()
                    .street(client.getAddress().getStreet())
                    .city(client.getAddress().getCity())
                    .postalCode(client.getAddress().getPostalCode())
                    .country(client.getAddress().getCountry())
                    .build();
        }

        PreferencesDto preferencesDto = null;
        if (client.getPreferences() != null) {
            preferencesDto = PreferencesDto.builder()
                    .notificationChannel(client.getPreferences().getNotificationChannel())
                    .language(client.getPreferences().getLanguage())
                    .build();
        }

        return ClientResponse.builder()
                .id(client.getId())
                .keycloakUserId(client.getKeycloakUserId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .birthDate(client.getBirthDate())
                .status(client.getStatus())
                .createdAt(client.getCreatedAt())
                .address(addressDto)
                .preferences(preferencesDto)
                .build();
    }
}