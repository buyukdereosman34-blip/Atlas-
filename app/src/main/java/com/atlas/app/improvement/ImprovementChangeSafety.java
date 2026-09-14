package com.atlas.app.improvement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImprovementChangeSafety {

    public ImprovementChangeSafetyReport evaluate(
            ImprovementChangeProposal proposal
    ) {
        if (proposal == null) {
            return report(
                    ImprovementChangeSafetyDecision.DENY,
                    "Change proposal is missing",
                    "No change will be evaluated",
                    "HIGH",
                    false,
                    Collections.emptyList(),
                    null
            );
        }

        if (isBlank(proposal.getWhat())
                || isBlank(proposal.getWhy())) {
            return report(
                    ImprovementChangeSafetyDecision.DENY,
                    "Change proposal is incomplete",
                    "The proposed change cannot be evaluated reliably",
                    "HIGH",
                    proposal.isReversible(),
                    Collections.emptyList(),
                    proposal.getValidation()
            );
        }

        if (proposal.getAffectedFiles().isEmpty()) {
            return report(
                    ImprovementChangeSafetyDecision.REQUIRE_REVIEW,
                    "No affected files were identified",
                    "The scope of the change is unknown",
                    proposal.getRisk(),
                    proposal.isReversible(),
                    Collections.emptyList(),
                    proposal.getValidation()
            );
        }

        if (isBlank(proposal.getExpectedResult())
                || proposal.getValidation() == null) {
            return report(
                    ImprovementChangeSafetyDecision.REQUIRE_REVIEW,
                    "Validation information is incomplete",
                    "The change cannot be automatically verified",
                    proposal.getRisk(),
                    proposal.isReversible(),
                    Collections.emptyList(),
                    proposal.getValidation()
            );
        }

        String normalizedRisk =
                proposal.getRisk() == null
                        ? ""
                        : proposal.getRisk().trim().toUpperCase();

        if ("HIGH".equals(normalizedRisk)
                || !proposal.isReversible()) {
            return report(
                    ImprovementChangeSafetyDecision.REQUIRE_REVIEW,
                    "The change has high risk or is not reversible",
                    "The change requires additional review before execution",
                    normalizedRisk.isEmpty()
                            ? "HIGH"
                            : normalizedRisk,
                    proposal.isReversible(),
                    Collections.emptyList(),
                    proposal.getValidation()
            );
        }

        return report(
                ImprovementChangeSafetyDecision.ALLOW,
                "Change proposal passed deterministic safety checks",
                "The change has a defined scope, expected result and validation path",
                normalizedRisk.isEmpty()
                        ? "UNKNOWN"
                        : normalizedRisk,
                proposal.isReversible(),
                Collections.emptyList(),
                proposal.getValidation()
        );
    }

    private ImprovementChangeSafetyReport report(
            ImprovementChangeSafetyDecision decision,
            String reason,
            String impact,
            String risk,
            boolean reversible,
            List<String> permissions,
            ImprovementValidation validation
    ) {
        return new ImprovementChangeSafetyReport(
                decision,
                reason,
                impact,
                risk,
                reversible,
                permissions,
                validation
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
