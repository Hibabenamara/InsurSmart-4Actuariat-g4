package tn.esprit.contratservice.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import tn.esprit.contratservice.entity.ContractStatus;
import tn.esprit.contratservice.repository.ContractRepository;
import tn.esprit.contratservice.repository.InsuranceTypeRepository;

/** Apparait dans /actuator/health sous le nom "catalogue". DOWN si contract_db ne repond pas. */
@Component
@RequiredArgsConstructor
public class CatalogueHealthIndicator implements HealthIndicator {

    private final InsuranceTypeRepository insuranceTypeRepository;
    private final ContractRepository contractRepository;

    @Override
    public Health health() {
        try {
            long types = insuranceTypeRepository.count();
            long active = contractRepository.countByStatus(ContractStatus.ACTIVE);
            return Health.up()
                    .withDetail("insuranceTypes", types)
                    .withDetail("activeContracts", active)
                    .build();
        } catch (Exception e) {
            return Health.down(e).withDetail("database", "contract_db injoignable").build();
        }
    }
}