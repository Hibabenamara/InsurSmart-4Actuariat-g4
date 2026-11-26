package tn.esprit.insursmart.claimservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.insursmart.claimservice.dto.*;
import tn.esprit.insursmart.claimservice.service.ClaimService;
import org.springframework.beans.factory.annotation.Value;
import java.net.URI;
import java.util.List;

/**
 * NOTE S5 : l'identite vient des en-tetes X-User-Id / X-User-Role (temporaire).
 * En S8 (Keycloak) : remplacer par le JWT (sub = userId, role = realm role).
 */
@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private static final String USER_ID = "X-User-Id";
    private static final String USER_ROLE = "X-User-Role";

    private final ClaimService claimService;

    @Value("${server.port}")
    private String port;

    @GetMapping("/hello")
    public String hello() {
        return "Hello I'm Claim Service on port " + port;
    }

    /** 201 | 400 | 404 | 409 | 503 */
    @PostMapping
    public ResponseEntity<ClaimResponse> create(@Valid @RequestBody ClaimRequest request,
                                                @RequestHeader(USER_ID) String userId) {
        ClaimResponse created = claimService.create(request, userId);
        return ResponseEntity.created(URI.create("/api/claims/" + created.id())).body(created);
    }

    @GetMapping
    public List<ClaimResponse> list(@RequestHeader(USER_ID) String userId,
                                    @RequestHeader(value = USER_ROLE, defaultValue = "CLIENT") String role) {
        return claimService.list(userId, role);
    }

    /** 200 | 403 | 404 */
    @GetMapping("/{id}")
    public ClaimResponse get(@PathVariable Long id,
                             @RequestHeader(USER_ID) String userId,
                             @RequestHeader(value = USER_ROLE, defaultValue = "CLIENT") String role) {
        return claimService.get(id, userId, role);
    }

    /** 201 | 400 | 404 | 409 | 413 */
    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse upload(@PathVariable Long id,
                                   @RequestPart("file") MultipartFile file,
                                   @RequestHeader(USER_ID) String userId,
                                   @RequestHeader(value = USER_ROLE, defaultValue = "CLIENT") String role) {
        return claimService.addDocument(id, file, userId, role);
    }

    /** 200 | 403 | 404 | 409  (PENDING -> UNDER_REVIEW) */
    @PatchMapping("/{id}/review")
    public ClaimResponse review(@PathVariable Long id,
                                @Valid @RequestBody(required = false) ReviewRequest request,
                                @RequestHeader(USER_ID) String agentId,
                                @RequestHeader(value = USER_ROLE, defaultValue = "CLIENT") String role) {
        return claimService.review(id, request == null ? null : request.comment(), agentId, role);
    }

    /** 200 | 400 | 403 | 404 | 409  (UNDER_REVIEW -> APPROVED | REJECTED) */
    @PatchMapping("/{id}/decision")
    public ClaimResponse decision(@PathVariable Long id,
                                  @Valid @RequestBody DecisionRequest request,
                                  @RequestHeader(USER_ID) String agentId,
                                  @RequestHeader(value = USER_ROLE, defaultValue = "CLIENT") String role) {
        return claimService.decide(id, request, agentId, role);
    }

    /** 200 | 403 | 404 */
    @GetMapping("/{id}/history")
    public List<HistoryResponse> history(@PathVariable Long id,
                                         @RequestHeader(USER_ID) String userId,
                                         @RequestHeader(value = USER_ROLE, defaultValue = "CLIENT") String role) {
        return claimService.history(id, userId, role);
    }
}
