package tn.esprit.contratservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.contratservice.entity.InsuranceCode;
import tn.esprit.contratservice.entity.InsuranceType;

public interface InsuranceTypeRepository extends JpaRepository<InsuranceType, Long> {
    boolean existsByCode(InsuranceCode code);
}