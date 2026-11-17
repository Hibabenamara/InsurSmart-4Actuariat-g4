package tn.esprit.insursmart.client_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.insursmart.client_service.entity.Client;
import tn.esprit.insursmart.client_service.entity.ClientStatus;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByKeycloakUserId(String keycloakUserId);

    boolean existsByKeycloakUserId(String keycloakUserId);

    boolean existsByEmail(String email);

    boolean existsByKeycloakUserIdAndStatus(String keycloakUserId, ClientStatus status);

    @Query("SELECT c FROM Client c WHERE " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Client> searchClients(@Param("search") String search, Pageable pageable);
}