package tn.esprit.insursmart.claimservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.insursmart.claimservice.entity.ClaimDocument;

import java.util.List;

public interface ClaimDocumentRepository extends JpaRepository<ClaimDocument, Long> {
    List<ClaimDocument> findByClaimId(Long claimId);
}
