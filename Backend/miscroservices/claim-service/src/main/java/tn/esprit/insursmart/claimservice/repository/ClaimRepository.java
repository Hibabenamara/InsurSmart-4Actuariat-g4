package tn.esprit.insursmart.claimservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.insursmart.claimservice.entity.Claim;
import tn.esprit.insursmart.claimservice.entity.ClaimStatus;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Claim> findAllByOrderByCreatedAtDesc();

    boolean existsByUserIdAndContractIdAndIncidentDateAndStatusIn(
            String userId, Long contractId, LocalDate incidentDate, Collection<ClaimStatus> statuses);
}
