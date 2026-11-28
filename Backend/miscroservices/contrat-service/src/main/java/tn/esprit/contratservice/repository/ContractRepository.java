package tn.esprit.contratservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.contratservice.entity.Contract;
import tn.esprit.contratservice.entity.ContractStatus;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByUserIdOrderByStartDateDesc(String userId);
    List<Contract> findByStatus(ContractStatus status);
    long countByStatus(ContractStatus status);
    boolean existsByGuarantees_Id(Long guaranteeId);
}