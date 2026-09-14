package com.atlas.app.improvement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImprovementChangeSafetyReport {

    private final ImprovementChangeSafetyDecision decision;
    private final String reason;
    private final String impact;
    private final String risk;
    private final boolean reversible;
    private final List<String> requiredPermissions;
    private final ImprovementValidation validation;

    public ImprovementChangeSafetyReport(
            ImprovementChangeSafetyDecision decision,
            String reason,
            String impact,
            String risk,
            boolean reversible,
            List<String> requiredPermissions,
            ImprovementValidation validation
    ) {
        this.decision = decision;
        this.reason = reason;
        this.impact = impact;
        this.risk = risk;
        this.reversible = reversible;
        this.requiredPermissions =
                requiredPermissions == null
                        ? Collections.emptyList()
                        : Collections.unmodifiableList(
                                new ArrayList<>(requiredPermissions)
                        );
        this.validation = validation;
    }

    public ImprovementChangeSafetyDecision getDecision() {
        return decision;
    }

    public String getReason() {
        return reason;
    }

    public String getImpact() {
        return impact;
    }

    public String getRisk() {
        return risk;
    }

    public boolean isReversible() {
        return reversible;
    }

    public List<String> getRequiredPermissions() {
        return requiredPermissions;
    }

    public ImprovementValidation getValidation() {
        return validation;
    }

    public boolean isAllowed() {
        return decision == ImprovementChangeSafetyDecision.ALLOW;
    }
}
