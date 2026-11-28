package tn.esprit.contratservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.contratservice.dto.GuaranteeRequest;
import tn.esprit.contratservice.dto.GuaranteeResponse;
import tn.esprit.contratservice.security.Roles;
import tn.esprit.contratservice.service.CatalogueService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/insurance-types/{typeId}/guarantees")
@RequiredArgsConstructor
public class GuaranteeController {

    private final CatalogueService catalogueService;

    @GetMapping
    public List<GuaranteeResponse> getAll(@PathVariable Long typeId) {
        return catalogueService.guarantees(typeId);
    }

    @GetMapping("/{gid}")
    public GuaranteeResponse getById(@PathVariable Long typeId, @PathVariable Long gid) {
        return catalogueService.getGuarantee(typeId, gid);
    }

    @PostMapping
    public ResponseEntity<GuaranteeResponse> create(@PathVariable Long typeId, @Valid @RequestBody GuaranteeRequest req,
                                                    @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        GuaranteeResponse created = catalogueService.addGuarantee(typeId, req, role);
        return ResponseEntity.created(URI.create("/api/insurance-types/" + typeId + "/guarantees/" + created.id()))
                .body(created);
    }

    @PutMapping("/{gid}")
    public GuaranteeResponse update(@PathVariable Long typeId, @PathVariable Long gid,
                                    @Valid @RequestBody GuaranteeRequest req,
                                    @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        return catalogueService.updateGuarantee(typeId, gid, req, role);
    }

    @DeleteMapping("/{gid}")
    public ResponseEntity<Void> delete(@PathVariable Long typeId, @PathVariable Long gid,
                                       @RequestHeader(value = Roles.USER_ROLE_HEADER, defaultValue = Roles.CLIENT) String role) {
        catalogueService.deleteGuarantee(typeId, gid, role);
        return ResponseEntity.noContent().build();
    }
}