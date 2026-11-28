package tn.esprit.contratservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.contratservice.dto.ContractResponse;
import tn.esprit.contratservice.dto.ContractUpdateRequest;
import tn.esprit.contratservice.entity.Contract;
import tn.esprit.contratservice.entity.ContractStatus;
import tn.esprit.contratservice.exception.*;
import tn.esprit.contratservice.mapper.ContractMapper;
import tn.esprit.contratservice.repository.ContractRepository;
import tn.esprit.contratservice.repository.SubscriptionRequestRepository;
import tn.esprit.contratservice.security.Roles;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ContractService {

    private static final Map<ContractStatus, Set<ContractStatus>> TRANSITIONS = Map.of(
            ContractStatus.DRAFT, Set.of(ContractStatus.ACTIVE),
            ContractStatus.ACTIVE, Set.of(ContractStatus.SUSPENDED, ContractStatus.EXPIRED),
            ContractStatus.SUSPENDED, Set.of(ContractStatus.ACTIVE, ContractStatus.EXPIRED),
            ContractStatus.EXPIRED, Set.of());

    private final ContractRepository contractRepository;
    private final SubscriptionRequestRepository subscriptionRepository;

    @Transactional(readOnly = true)
    public List<ContractResponse> listAll(ContractStatus status, String role) {
        Roles.require(role, Roles.AGENT, Roles.ADMIN);
        List<Contract> list = status == null ? contractRepository.findAll() : contractRepository.findByStatus(status);
        return list.stream().map(ContractMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ContractResponse> listForClient(String clientId, String requesterId, String role) {
        Roles.requireOwnerOrStaff(clientId, requesterId, role);
        return contractRepository.findByUserIdOrderByStartDateDesc(clientId).stream()
                .map(ContractMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ContractResponse get(Long id, String requesterId, String role) {
        Contract c = find(id);
        Roles.requireOwnerOrStaff(c.getUserId(), requesterId, role);
        return ContractMapper.toResponse(c);
    }

    @Transactional
    public ContractResponse update(Long id, ContractUpdateRequest req, String role) {
        Roles.require(role, Roles.AGENT, Roles.ADMIN);
        Contract c = find(id);
        if (c.getStatus() == ContractStatus.EXPIRED) {
            throw new InvalidStatusTransitionException("Contrat EXPIRED : modification impossible");
        }
        if (!req.endDate().isAfter(req.startDate())) {
            throw new BadRequestException("endDate doit etre apres startDate");
        }
        c.setStartDate(req.startDate());
        c.setEndDate(req.endDate());
        c.setPremium(req.premium());
        return ContractMapper.toResponse(contractRepository.save(c));
    }

    @Transactional
    public ContractResponse updateStatus(Long id, ContractStatus target, String role) {
        Roles.require(role, Roles.AGENT, Roles.ADMIN);
        Contract c = find(id);
        if (!TRANSITIONS.getOrDefault(c.getStatus(), Set.of()).contains(target)) {
            throw new InvalidStatusTransitionException("Transition interdite : " + c.getStatus() + " -> " + target);
        }
        c.setStatus(target);
        return ContractMapper.toResponse(contractRepository.save(c));
    }

    @Transactional
    public void delete(Long id, String role) {
        Roles.require(role, Roles.ADMIN);
        Contract c = find(id);
        subscriptionRepository.findByContractId(id).ifPresent(s -> s.setContract(null));
        subscriptionRepository.flush();
        contractRepository.delete(c);
    }

    Contract find(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat " + id + " introuvable"));
    }
}