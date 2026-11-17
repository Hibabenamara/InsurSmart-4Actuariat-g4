package tn.esprit.insursmart.client_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.insursmart.client_service.entity.CustomerPreferences;

import java.util.Optional;

public interface CustomerPreferencesRepository extends JpaRepository<CustomerPreferences, Long> {

    Optional<CustomerPreferences> findByClientKeycloakUserId(String keycloakUserId);
}