package tn.esprit.contratservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.contratservice.dto.*;
import tn.esprit.contratservice.entity.SubscriptionStatus;
import tn.esprit.contratservice.security.Roles;
import tn.esprit.contratservice.service.SubscriptionService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(@Valid @RequestBody SubscriptionCreateRequest req,
                                                       @RequestHeader(Roles.USER_ID_HEADER) String userId,
                                                       @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        SubscriptionResponse created = subscriptionService.create(req, userId, role);
        return ResponseEntity.created(URI.create("/api/subscriptions/" + created.id())).body(created);
    }

    @GetMapping
    public List<SubscriptionResponse> getAll(@RequestParam(required = false) SubscriptionStatus status,
                                             @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        return subscriptionService.listByStatus(status, role);
    }

    @GetMapping("/me")
    public List<SubscriptionResponse> getMine(@RequestHeader(Roles.USER_ID_HEADER) String userId) {
        return subscriptionService.listMine(userId);
    }

    @GetMapping("/{id}")
    public SubscriptionResponse getById(@PathVariable Long id,
                                        @RequestHeader(Roles.USER_ID_HEADER) String userId,
                                        @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        return subscriptionService.get(id, userId, role);
    }

    @PutMapping("/{id}")
    public SubscriptionResponse update(@PathVariable Long id, @Valid @RequestBody SubscriptionCreateRequest req,
                                       @RequestHeader(Roles.USER_ID_HEADER) String userId,
                                       @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        return subscriptionService.update(id, req, userId, role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader(Roles.USER_ID_HEADER) String userId,
                                       @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        subscriptionService.delete(id, userId, role);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/decision")
    public SubscriptionResponse decide(@PathVariable Long id, @Valid @RequestBody SubscriptionDecisionRequest req,
                                       @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        return subscriptionService.decide(id, req, role);
    }
}