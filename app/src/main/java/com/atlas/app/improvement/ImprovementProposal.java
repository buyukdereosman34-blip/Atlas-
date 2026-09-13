package com.atlas.app.improvement;

public class ImprovementProposal {

    private final String title;
    private final String reason;
    private final String suggestedAction;
    private final String validationCriteria;

    public ImprovementProposal(
            String title,
            String reason,
            String suggestedAction,
            String validationCriteria
    ) {
        this.title = title;
        this.reason = reason;
        this.suggestedAction = suggestedAction;
        this.validationCriteria = validationCriteria;
    }

    public String getTitle() {
        return title;
    }

    public String getReason() {
        return reason;
    }

    public String getSuggestedAction() {
        return suggestedAction;
    }

    public String getValidationCriteria() {
        return validationCriteria;
    }
}
