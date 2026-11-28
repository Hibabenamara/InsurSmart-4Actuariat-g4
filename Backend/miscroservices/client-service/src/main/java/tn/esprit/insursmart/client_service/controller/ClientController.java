package tn.esprit.insursmart.client_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.insursmart.client_service.dto.*;
import tn.esprit.insursmart.client_service.service.ClientService;
import org.springdoc.core.annotations.ParameterObject;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @Value("${server.port:8081}")
    private String port;

    @GetMapping("/hello")
    public String hello() {
        return "Client Service is running on port " + port + "!";
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.create(request));
    }

    @GetMapping
    public List<ClientResponse> getAll() {
        return clientService.getAll();
    }

    @GetMapping("/search")
    public Page<ClientResponse> search(@RequestParam String q, @ParameterObject Pageable pageable) {
        return clientService.search(q, pageable);
    }

    @GetMapping("/{id}")
    public ClientResponse getById(@PathVariable Long id) {
        return clientService.getById(id);
    }

    @GetMapping("/keycloak/{keycloakUserId}")
    public ClientResponse getByKeycloakUserId(@PathVariable String keycloakUserId) {
        return clientService.getByKeycloakUserId(keycloakUserId);
    }

    @PutMapping("/{id}")
    public ClientResponse update(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        return clientService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public ClientResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return clientService.updateStatus(id, request.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

}