package tn.esprit.insursmart.claimservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.insursmart.claimservice.dto.*;
import tn.esprit.insursmart.claimservice.entity.*;
import tn.esprit.insursmart.claimservice.exception.*;
import tn.esprit.insursmart.claimservice.repository.ClaimRepository;
import tn.esprit.insursmart.claimservice.repository.ClaimStatusHistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimStatusHistoryRepository historyRepository;
    private final ContractEligibilityService eligibilityService;
    private final FileStorageService storageService;

    // ---------------------------------------------------------------- declaration
    @Transactional
    public ClaimResponse create(ClaimRequest req, String userId) {
        eligibilityService.assertEligible(req.contractId(), userId);

        boolean duplicate = claimRepository.existsByUserIdAndContractIdAndIncidentDateAndStatusIn(
                userId, req.contractId(), req.incidentDate(),
                List.of(ClaimStatus.PENDING, ClaimStatus.UNDER_REVIEW));
        if (duplicate) {
            throw new InvalidStatusTransitionException(
                    "Un sinistre identique est deja en cours pour ce contrat et cette date");
        }

        Claim claim = Claim.builder()
                .userId(userId)
                .contractId(req.contractId())
                .incidentDate(req.incidentDate())
                .description(req.description())
                .incidentLocation(req.incidentLocation())
                .estimatedDamage(req.estimatedDamage())
                .build();
        addHistory(claim, null, ClaimStatus.PENDING, userId, "Sinistre declare");
        return ClaimResponse.from(claimRepository.save(claim));
    }

    // ---------------------------------------------------------------- consultation
    @Transactional(readOnly = true)
    public List<ClaimResponse> list(String userId, String role) {
        List<Claim> claims = isStaff(role)
                ? claimRepository.findAllByOrderByCreatedAtDesc()
                : claimRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return claims.stream().map(ClaimResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ClaimResponse get(Long id, String userId, String role) {
        Claim claim = findOrThrow(id);
        checkAccess(claim, userId, role);
        return ClaimResponse.from(claim);
    }

    @Transactional(readOnly = true)
    public List<HistoryResponse> history(Long id, String userId, String role) {
        Claim claim = findOrThrow(id);
        checkAccess(claim, userId, role);
        return historyRepository.findByClaimIdOrderByChangedAtAscIdAsc(id)
                .stream().map(HistoryResponse::from).toList();
    }

    // ---------------------------------------------------------------- pieces jointes
    @Transactional
    public DocumentResponse addDocument(Long id, MultipartFile file, String userId, String role) {
        Claim claim = findOrThrow(id);
        checkAccess(claim, userId, role);
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Le fichier est vide");
        }
        if (claim.getStatus() == ClaimStatus.REJECTED || claim.getStatus() == ClaimStatus.PAID) {
            throw new InvalidStatusTransitionException(
                    "Impossible d'ajouter une piece a un sinistre " + claim.getStatus());
        }
        ClaimDocument doc = ClaimDocument.builder()
                .fileName(file.getOriginalFilename() == null ? "piece" : file.getOriginalFilename())
                .fileUrl(storageService.store(id, file))
                .claim(claim)
                .build();
        claim.getDocuments().add(doc);
        claimRepository.saveAndFlush(claim);
        return DocumentResponse.from(doc);
    }

    // ---------------------------------------------------------------- agent : examen
    @Transactional
    public ClaimResponse review(Long id, String comment, String agentId, String role) {
        requireStaff(role);
        Claim claim = findOrThrow(id);
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new InvalidStatusTransitionException(
                    "Seul un sinistre PENDING peut passer en examen (statut actuel : " + claim.getStatus() + ")");
        }
        changeStatus(claim, ClaimStatus.UNDER_REVIEW, agentId,
                comment == null || comment.isBlank() ? "Dossier pris en charge" : comment);
        return ClaimResponse.from(claim);
    }

    // ---------------------------------------------------------------- agent : decision
    @Transactional
    public ClaimResponse decide(Long id, DecisionRequest req, String agentId, String role) {
        requireStaff(role);
        if (req.decision() != ClaimStatus.APPROVED && req.decision() != ClaimStatus.REJECTED) {
            throw new BadRequestException("decision doit valoir APPROVED ou REJECTED");
        }
        if (req.decision() == ClaimStatus.REJECTED && (req.reason() == null || req.reason().isBlank())) {
            throw new BadRequestException("reason est obligatoire en cas de rejet");
        }
        Claim claim = findOrThrow(id);
        if (claim.getStatus() != ClaimStatus.UNDER_REVIEW) {
            throw new InvalidStatusTransitionException(
                    "Seul un sinistre UNDER_REVIEW peut etre decide (statut actuel : " + claim.getStatus() + ")");
        }
        claim.setDecisionReason(req.reason());
        changeStatus(claim, req.decision(), agentId,
                req.reason() == null || req.reason().isBlank() ? "Sinistre accepte" : req.reason());
        // TODO S7 : publier claim.approved / claim.rejected sur RabbitMQ (exchange insursmart.events)
        return ClaimResponse.from(claim);
    }

    // ---------------------------------------------------------------- helpers
    private Claim findOrThrow(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinistre introuvable : " + id));
    }

    private void changeStatus(Claim claim, ClaimStatus newStatus, String by, String comment) {
        ClaimStatus old = claim.getStatus();
        claim.setStatus(newStatus);
        addHistory(claim, old, newStatus, by, comment);
    }

    private void addHistory(Claim claim, ClaimStatus oldStatus, ClaimStatus newStatus, String by, String comment) {
        claim.getHistory().add(ClaimStatusHistory.builder()
                .claim(claim).oldStatus(oldStatus).newStatus(newStatus)
                .changedBy(by).comment(comment).build());
    }

    private boolean isStaff(String role) {
        return "AGENT".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }

    private void requireStaff(String role) {
        if (!isStaff(role)) {
            throw new ForbiddenOperationException("Operation reservee aux agents");
        }
    }

    private void checkAccess(Claim claim, String userId, String role) {
        if (!isStaff(role) && !claim.getUserId().equals(userId)) {
            throw new ForbiddenOperationException("Ce sinistre ne vous appartient pas");
        }
    }
}
