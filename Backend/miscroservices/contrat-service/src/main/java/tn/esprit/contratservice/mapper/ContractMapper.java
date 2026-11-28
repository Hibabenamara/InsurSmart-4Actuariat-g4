package tn.esprit.contratservice.mapper;

import tn.esprit.contratservice.dto.*;
import tn.esprit.contratservice.entity.*;
import java.util.List;

public final class ContractMapper {

    private ContractMapper() {}

    public static GuaranteeResponse toResponse(Guarantee g) {
        return new GuaranteeResponse(g.getId(), g.getName(), g.getCoverageLimit(), g.getDeductible());
    }

    public static List<GuaranteeResponse> toResponses(List<Guarantee> list) {
        return list.stream().map(ContractMapper::toResponse).toList();
    }

    public static InsuranceTypeResponse toResponse(InsuranceType t) {
        return new InsuranceTypeResponse(t.getId(), t.getCode(), t.getName(), t.getDescription(),
                t.getBasePremium(), toResponses(t.getGuarantees()));
    }

    public static SubscriptionResponse toResponse(SubscriptionRequest s) {
        return new SubscriptionResponse(s.getId(), s.getUserId(), s.getInsuranceType().getId(),
                s.getInsuranceType().getName(), s.getRequestedStart(), s.getStatus(), s.getDecisionReason(),
                s.getCreatedAt(), s.getContract() == null ? null : s.getContract().getId());
    }

    public static ContractResponse toResponse(Contract c) {
        return new ContractResponse(c.getId(), c.getUserId(), c.getPolicyNumber(), c.getStartDate(),
                c.getEndDate(), c.getPremium(), c.getStatus(), toResponses(c.getGuarantees()));
    }
}