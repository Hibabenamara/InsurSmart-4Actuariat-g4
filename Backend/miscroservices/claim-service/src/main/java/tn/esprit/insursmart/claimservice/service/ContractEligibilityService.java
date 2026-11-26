package tn.esprit.insursmart.claimservice.service;

import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tn.esprit.insursmart.claimservice.exception.ServiceUnavailableException;

@Service
public class ContractEligibilityService {

    private final RestClient client;
    private final CircuitBreakerFactory<?, ?> cbFactory;

    public ContractEligibilityService(@LoadBalanced RestClient.Builder builder,
                                      CircuitBreakerFactory<?, ?> cbFactory) {
        this.client = builder.baseUrl("http://client-service").build();
        this.cbFactory = cbFactory;
    }

    public void assertEligible(Long contractId, String userId) {
        cbFactory.create("clientService").run(
                () -> client.get().uri("/api/clients/hello").retrieve().toBodilessEntity(),
                ex -> {
                    throw new ServiceUnavailableException(
                            "Service client indisponible, réessayez plus tard (fallback)");
                });
    }
}