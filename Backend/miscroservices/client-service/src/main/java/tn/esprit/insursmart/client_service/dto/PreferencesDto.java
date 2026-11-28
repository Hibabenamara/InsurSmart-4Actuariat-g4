package tn.esprit.insursmart.client_service.dto;

import lombok.*;
import tn.esprit.insursmart.client_service.entity.NotificationChannel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferencesDto {
    private NotificationChannel notificationChannel;
    private String language;
}