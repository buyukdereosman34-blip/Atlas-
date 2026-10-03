package com.atlas.app.improvement;

public class ImprovementFailureClassification {

    private final ImprovementFailureCategory category;
    private final String reason;
    private final String evidence;

    public ImprovementFailureClassification(
            ImprovementFailureCategory category,
            String reason,
            String evidence
    ) {
        this.category = category == null
                ? ImprovementFailureCategory.UNKNOWN
                : category;
        this.reason = reason == null ? "" : reason;
        this.evidence = evidence == null ? "" : evidence;
    }

    public ImprovementFailureCategory getCategory() {
        return category;
    }

    public String getReason() {
        return reason;
    }

    public String getEvidence() {
        return evidence;
    }

    public boolean isRetryable() {
        return category.isRetryable();
    }

    public boolean isEnvironmentIssue() {
        return category.isEnvironmentIssue();
    }

    @Override
    public String toString() {
        return "FailureClassification["
                + category
                + ": "
                + reason
                + (evidence.isEmpty() ? "" : " | " + evidence)
                + "]";
    }
}
