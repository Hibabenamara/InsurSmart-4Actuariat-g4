package tn.esprit.contratservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.contratservice.dto.InsuranceTypeRequest;
import tn.esprit.contratservice.dto.InsuranceTypeResponse;
import tn.esprit.contratservice.security.Roles;
import tn.esprit.contratservice.service.CatalogueService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/insurance-types")
@RequiredArgsConstructor
public class InsuranceTypeController {

    private final CatalogueService catalogueService;

    @GetMapping
    public List<InsuranceTypeResponse> getAll() {
        return catalogueService.list();
    }

    @GetMapping("/{id}")
    public InsuranceTypeResponse getById(@PathVariable Long id) {
        return catalogueService.get(id);
    }

    @PostMapping
    public ResponseEntity<InsuranceTypeResponse> create(@Valid @RequestBody InsuranceTypeRequest req,
                                                        @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        InsuranceTypeResponse created = catalogueService.create(req, role);
        return ResponseEntity.created(URI.create("/api/insurance-types/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public InsuranceTypeResponse update(@PathVariable Long id, @Valid @RequestBody InsuranceTypeRequest req,
                                        @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        return catalogueService.update(id, req, role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        catalogueService.delete(id, role);
        return ResponseEntity.noContent().build();
    }
}