package tn.esprit.contratservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.contratservice.entity.SubscriptionRequest;
import tn.esprit.contratservice.entity.SubscriptionStatus;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRequestRepository extends JpaRepository<SubscriptionRequest, Long> {
    List<SubscriptionRequest> findByStatusOrderByCreatedAtAsc(SubscriptionStatus status);
    List<SubscriptionRequest> findByUserIdOrderByCreatedAtDesc(String userId);
    boolean existsByInsuranceTypeId(Long insuranceTypeId);
    Optional<SubscriptionRequest> findByContractId(Long contractId);
}