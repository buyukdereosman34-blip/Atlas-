package com.atlas.app.improvement;

public interface ApprovalCallback {
    boolean requestApproval(
            ImprovementChangeProposal proposal,
            String diffSummary
    );
}
