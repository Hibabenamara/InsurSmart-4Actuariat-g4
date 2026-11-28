package tn.esprit.contratservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.contratservice.dto.*;
import tn.esprit.contratservice.entity.Guarantee;
import tn.esprit.contratservice.entity.InsuranceType;
import tn.esprit.contratservice.exception.ConflictException;
import tn.esprit.contratservice.exception.ResourceNotFoundException;
import tn.esprit.contratservice.mapper.ContractMapper;
import tn.esprit.contratservice.repository.ContractRepository;
import tn.esprit.contratservice.repository.InsuranceTypeRepository;
import tn.esprit.contratservice.repository.SubscriptionRequestRepository;
import tn.esprit.contratservice.security.Roles;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogueService {

    private final InsuranceTypeRepository insuranceTypeRepository;
    private final SubscriptionRequestRepository subscriptionRepository;
    private final ContractRepository contractRepository;

    // ---------- InsuranceType ----------

    @Transactional(readOnly = true)
    public List<InsuranceTypeResponse> list() {
        return insuranceTypeRepository.findAll().stream().map(ContractMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public InsuranceTypeResponse get(Long id) {
        return ContractMapper.toResponse(find(id));
    }

    @Transactional
    public InsuranceTypeResponse create(InsuranceTypeRequest req, String role) {
        Roles.require(role, Roles.ADMIN);
        if (insuranceTypeRepository.existsByCode(req.code())) {
            throw new ConflictException("Le code " + req.code() + " existe deja");
        }
        InsuranceType type = InsuranceType.builder()
                .code(req.code()).name(req.name())
                .description(req.description()).basePremium(req.basePremium())
                .build();
        return ContractMapper.toResponse(insuranceTypeRepository.save(type));
    }

    @Transactional
    public InsuranceTypeResponse update(Long id, InsuranceTypeRequest req, String role) {
        Roles.require(role, Roles.ADMIN);
        InsuranceType type = find(id);
        if (type.getCode() != req.code() && insuranceTypeRepository.existsByCode(req.code())) {
            throw new ConflictException("Le code " + req.code() + " existe deja");
        }
        type.setCode(req.code());
        type.setName(req.name());
        type.setDescription(req.description());
        type.setBasePremium(req.basePremium());
        return ContractMapper.toResponse(insuranceTypeRepository.save(type));
    }

    @Transactional
    public void delete(Long id, String role) {
        Roles.require(role, Roles.ADMIN);
        InsuranceType type = find(id);
        if (subscriptionRepository.existsByInsuranceTypeId(id)) {
            throw new ConflictException("Type utilise par des demandes de souscription : suppression impossible");
        }
        for (Guarantee g : type.getGuarantees()) {
            if (contractRepository.existsByGuarantees_Id(g.getId())) {
                throw new ConflictException("La garantie '" + g.getName() + "' couvre des contrats");
            }
        }
        insuranceTypeRepository.delete(type);
    }

    // ---------- Guarantee ----------

    @Transactional(readOnly = true)
    public List<GuaranteeResponse> guarantees(Long typeId) {
        return ContractMapper.toResponses(find(typeId).getGuarantees());
    }

    @Transactional(readOnly = true)
    public GuaranteeResponse getGuarantee(Long typeId, Long gid) {
        return ContractMapper.toResponse(findGuarantee(find(typeId), gid));
    }

    @Transactional
    public GuaranteeResponse addGuarantee(Long typeId, GuaranteeRequest req, String role) {
        Roles.require(role, Roles.ADMIN);
        InsuranceType type = find(typeId);
        Guarantee g = Guarantee.builder()
                .name(req.name()).coverageLimit(req.coverageLimit()).deductible(req.deductible())
                .build();
        type.addGuarantee(g);
        insuranceTypeRepository.saveAndFlush(type);
        return ContractMapper.toResponse(g);
    }

    @Transactional
    public GuaranteeResponse updateGuarantee(Long typeId, Long gid, GuaranteeRequest req, String role) {
        Roles.require(role, Roles.ADMIN);
        Guarantee g = findGuarantee(find(typeId), gid);
        g.setName(req.name());
        g.setCoverageLimit(req.coverageLimit());
        g.setDeductible(req.deductible());
        return ContractMapper.toResponse(g);
    }

    @Transactional
    public void deleteGuarantee(Long typeId, Long gid, String role) {
        Roles.require(role, Roles.ADMIN);
        InsuranceType type = find(typeId);
        Guarantee g = findGuarantee(type, gid);
        if (contractRepository.existsByGuarantees_Id(gid)) {
            throw new ConflictException("La garantie '" + g.getName() + "' couvre des contrats");
        }
        type.getGuarantees().remove(g);   // orphanRemoval = true : supprime la ligne
    }

    // ---------- utilitaires ----------

    InsuranceType find(Long id) {
        return insuranceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type d'assurance " + id + " introuvable"));
    }

    private Guarantee findGuarantee(InsuranceType type, Long gid) {
        return type.getGuarantees().stream().filter(g -> g.getId().equals(gid)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Garantie " + gid + " introuvable"));
    }
}