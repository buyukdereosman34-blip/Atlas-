package com.atlas.app.improvement;

public class ImprovementProposal {

    private final String title;
    private final String reason;
    private final String suggestedAction;
    private final ImprovementValidation validation;
    private ImprovementProposalStatus status;

    public ImprovementProposal(
            String title,
            String reason,
            String suggestedAction,
            ImprovementValidation validation
    ) {
        this.title = title;
        this.reason = reason;
        this.suggestedAction = suggestedAction;
        this.validation = validation;
        this.status = ImprovementProposalStatus.PROPOSED;
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

    public ImprovementValidation getValidation() {
        return validation;
    }

    public ImprovementProposalStatus getStatus() {
        return status;
    }

    public void approve() {
        if (status == ImprovementProposalStatus.PROPOSED) {
            status = ImprovementProposalStatus.APPROVED;
        }
    }

    public void reject() {
        if (status == ImprovementProposalStatus.PROPOSED) {
            status = ImprovementProposalStatus.REJECTED;
        }
    }
}
