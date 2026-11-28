package tn.esprit.contratservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.contratservice.entity.*;
import tn.esprit.contratservice.repository.InsuranceTypeRepository;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final InsuranceTypeRepository repo;

    @Override
    @Transactional
    public void run(String... args) {
        if (repo.count() > 0) return;

        InsuranceType auto = InsuranceType.builder().code(InsuranceCode.AUTO).name("Assurance Auto")
                .description("Collision, vol, incendie").basePremium(new BigDecimal("650.00")).build();
        auto.addGuarantee(g("Dommages collision", "5000.00", "200.00"));
        auto.addGuarantee(g("Vol et incendie", "15000.00", "500.00"));

        InsuranceType home = InsuranceType.builder().code(InsuranceCode.HOME).name("Assurance Habitation")
                .description("Degats des eaux, incendie").basePremium(new BigDecimal("320.00")).build();
        home.addGuarantee(g("Degats des eaux", "8000.00", "150.00"));

        repo.save(auto);
        repo.save(home);
    }

    private static Guarantee g(String name, String limit, String deductible) {
        return Guarantee.builder().name(name)
                .coverageLimit(new BigDecimal(limit)).deductible(new BigDecimal(deductible)).build();
    }
}