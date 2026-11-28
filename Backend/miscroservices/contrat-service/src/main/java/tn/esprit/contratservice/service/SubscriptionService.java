package tn.esprit.contratservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.contratservice.dto.*;
import tn.esprit.contratservice.entity.*;
import tn.esprit.contratservice.exception.*;
import tn.esprit.contratservice.mapper.ContractMapper;
import tn.esprit.contratservice.repository.ContractRepository;
import tn.esprit.contratservice.repository.SubscriptionRequestRepository;
import tn.esprit.contratservice.security.Roles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRequestRepository subscriptionRepository;
    private final ContractRepository contractRepository;
    private final CatalogueService catalogueService;

    @Transactional
    public SubscriptionResponse create(SubscriptionCreateRequest req, String userId, String role) {
        Roles.require(role, Roles.CLIENT);
        InsuranceType type = catalogueService.find(req.insuranceTypeId());
        SubscriptionRequest s = SubscriptionRequest.builder()
                .userId(userId).requestedStart(req.requestedStart()).insuranceType(type)
                .build();
        return ContractMapper.toResponse(subscriptionRepository.save(s));
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> listByStatus(SubscriptionStatus status, String role) {
        Roles.require(role, Roles.AGENT, Roles.ADMIN);
        List<SubscriptionRequest> list = status == null
                ? subscriptionRepository.findAll()
                : subscriptionRepository.findByStatusOrderByCreatedAtAsc(status);
        return list.stream().map(ContractMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> listMine(String userId) {
        return subscriptionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(ContractMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse get(Long id, String userId, String role) {
        SubscriptionRequest s = find(id);
        Roles.requireOwnerOrStaff(s.getUserId(), userId, role);
        return ContractMapper.toResponse(s);
    }

    /** Modifiable seulement tant que la demande est SUBMITTED */
    @Transactional
    public SubscriptionResponse update(Long id, SubscriptionCreateRequest req, String userId, String role) {
        SubscriptionRequest s = find(id);
        Roles.requireOwnerOrStaff(s.getUserId(), userId, role);
        requireSubmitted(s);
        s.setInsuranceType(catalogueService.find(req.insuranceTypeId()));
        s.setRequestedStart(req.requestedStart());
        return ContractMapper.toResponse(subscriptionRepository.save(s));
    }

    /** Annulation seulement tant que la demande est SUBMITTED */
    @Transactional
    public void delete(Long id, String userId, String role) {
        SubscriptionRequest s = find(id);
        Roles.requireOwnerOrStaff(s.getUserId(), userId, role);
        requireSubmitted(s);
        subscriptionRepository.delete(s);
    }

    /** APPROVED : cree le contrat (DRAFT -> ACTIVE). REJECTED : motif obligatoire. */
    @Transactional
    public SubscriptionResponse decide(Long id, SubscriptionDecisionRequest req, String role) {
        Roles.require(role, Roles.AGENT, Roles.ADMIN);
        SubscriptionRequest s = find(id);
        requireSubmitted(s);
        if (req.decision() == SubscriptionStatus.SUBMITTED) {
            throw new BadRequestException("decision doit etre APPROVED ou REJECTED");
        }
        if (req.decision() == SubscriptionStatus.REJECTED && (req.reason() == null || req.reason().isBlank())) {
            throw new BadRequestException("Un motif (reason) est obligatoire pour un refus");
        }
        s.setStatus(req.decision());
        s.setDecisionReason(req.reason());
        if (req.decision() == SubscriptionStatus.APPROVED) {
            s.setContract(createContract(s));
        }
        return ContractMapper.toResponse(subscriptionRepository.save(s));
    }

    private Contract createContract(SubscriptionRequest s) {
        InsuranceType type = s.getInsuranceType();
        if (type.getGuarantees().isEmpty()) {
            throw new InvalidStatusTransitionException("Le type " + type.getCode() + " n'a aucune garantie");
        }
        LocalDate start = s.getRequestedStart();
        Contract contract = Contract.builder()
                .userId(s.getUserId())
                .policyNumber("POL-" + start.getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .startDate(start)
                .endDate(start.plusYears(1).minusDays(1))
                .premium(type.getBasePremium())
                .status(ContractStatus.ACTIVE)      // validation de l'agent : DRAFT -> ACTIVE
                .guarantees(new ArrayList<>(type.getGuarantees()))
                .build();
        return contractRepository.save(contract);
    }

    private void requireSubmitted(SubscriptionRequest s) {
        if (s.getStatus() != SubscriptionStatus.SUBMITTED) {
            throw new InvalidStatusTransitionException("La demande " + s.getId() + " est deja " + s.getStatus());
        }
    }

    private SubscriptionRequest find(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande " + id + " introuvable"));
    }
}