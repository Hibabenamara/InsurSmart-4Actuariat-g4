package tn.esprit.insursmart.claimservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.insursmart.claimservice.entity.ClaimStatusHistory;

import java.util.List;

public interface ClaimStatusHistoryRepository extends JpaRepository<ClaimStatusHistory, Long> {
    List<ClaimStatusHistory> findByClaimIdOrderByChangedAtAscIdAsc(Long claimId);
}
