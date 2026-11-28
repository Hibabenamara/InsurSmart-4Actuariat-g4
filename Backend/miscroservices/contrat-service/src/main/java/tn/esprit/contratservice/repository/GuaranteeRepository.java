package tn.esprit.contratservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.contratservice.entity.Guarantee;

import java.util.List;

public interface GuaranteeRepository extends JpaRepository<Guarantee, Long> {
    List<Guarantee> findByInsuranceTypeId(Long insuranceTypeId);
}