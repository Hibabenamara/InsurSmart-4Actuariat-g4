package tn.esprit.insursmart.claimservice.service;

import org.springframework.stereotype.Service;

/**
 * Verifie que le contrat est ACTIVE et appartient au client.
 * MOCK pour le palier S5 : a remplacer en S6 par un client OpenFeign vers
 * GET /internal/contracts/{id}/eligibility (contract-service), avec circuit breaker :
 *  - contract-service injoignable  -> throw new ServiceUnavailableException(...)      (503)
 *  - contrat non eligible          -> throw new InvalidStatusTransitionException(...) (409)
 *  - contrat inexistant            -> throw new ResourceNotFoundException(...)        (404)
 */
@Service
public class ContractEligibilityService {

    public void assertEligible(Long contractId, String userId) {
        // TODO S6 : appel OpenFeign
    }
}
