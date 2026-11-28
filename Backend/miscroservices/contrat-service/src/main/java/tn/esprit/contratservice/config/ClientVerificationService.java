package tn.esprit.contratservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tn.esprit.contratservice.exception.ResourceNotFoundException;
import tn.esprit.contratservice.exception.ServiceUnavailableException;

/** S1 : GET /internal/clients/{userId}/exists sur client-service (200 = connu, 404 = inconnu). */
@Slf4j
@Service
public class ClientVerificationService {

    public record ClientCheck(Boolean exists, Boolean active) {}

    private final RestClient client;
    private final CircuitBreakerFactory<?, ?> cbFactory;
    private final boolean enabled;

    public ClientVerificationService(@LoadBalanced RestClient.Builder builder,
                                     CircuitBreakerFactory<?, ?> cbFactory,
                                     @Value("${contract.client-check.base-url:http://client-service}") String baseUrl,
                                     @Value("${contract.client-check.enabled:true}") boolean enabled) {
        this.client = builder.baseUrl(baseUrl).build();
        this.cbFactory = cbFactory;
        this.enabled = enabled;
    }

    public void assertClientExists(String userId) {
        if (!enabled) {
            log.warn("Verification S1 desactivee pour userId={}", userId);
            return;
        }
        Boolean known = cbFactory.create("clientService").run(
                () -> callClientService(userId),
                ex -> {
                    log.warn("S1 en echec ({}) -> fallback 503", ex.toString());
                    throw new ServiceUnavailableException(
                            "Service client indisponible, souscription refusee. Reessayez plus tard (fallback)");
                });
        if (!Boolean.TRUE.equals(known)) {
            throw new ResourceNotFoundException("Client " + userId + " introuvable ou inactif");
        }
    }

    private Boolean callClientService(String userId) {
        return client.get()
                .uri("/internal/clients/{userId}/exists", userId)
                .exchange((request, response) -> {
                    int status = response.getStatusCode().value();
                    if (status == 404) {
                        return false;
                    }
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        throw new IllegalStateException("client-service a repondu " + status);
                    }
                    ClientCheck body = response.bodyTo(ClientCheck.class);
                    return body == null
                            || (!Boolean.FALSE.equals(body.exists()) && !Boolean.FALSE.equals(body.active()));
                });
    }
}