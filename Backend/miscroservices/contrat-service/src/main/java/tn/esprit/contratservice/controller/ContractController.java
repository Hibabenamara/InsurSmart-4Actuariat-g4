package tn.esprit.contratservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.contratservice.dto.*;
import tn.esprit.contratservice.entity.ContractStatus;
import tn.esprit.contratservice.security.Roles;
import tn.esprit.contratservice.service.ContractService;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @Value("${server.port:8082}")
    private String port;

    @GetMapping("/hello")
    public String hello() {
        return "Hello I'm Contract Service on port " + port;
    }

    @GetMapping
    public List<ContractResponse> getAll(@RequestParam(required = false) ContractStatus status,
                                         @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        return contractService.listAll(status, role);
    }

    @GetMapping("/client/{userId}")
    public List<ContractResponse> getByClient(@PathVariable String userId,
                                              @RequestHeader(Roles.USER_ID_HEADER) String requesterId,
                                              @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        return contractService.listForClient(userId, requesterId, role);
    }

    @GetMapping("/{id}")
    public ContractResponse getById(@PathVariable Long id,
                                    @RequestHeader(Roles.USER_ID_HEADER) String requesterId,
                                    @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        return contractService.get(id, requesterId, role);
    }

    @PutMapping("/{id}")
    public ContractResponse update(@PathVariable Long id, @Valid @RequestBody ContractUpdateRequest req,
                                   @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        return contractService.update(id, req, role);
    }

    @PatchMapping("/{id}/status")
    public ContractResponse updateStatus(@PathVariable Long id, @Valid @RequestBody ContractStatusRequest req,
                                         @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        return contractService.updateStatus(id, req.status(), role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        contractService.delete(id, role);
        return ResponseEntity.noContent().build();
    }



}