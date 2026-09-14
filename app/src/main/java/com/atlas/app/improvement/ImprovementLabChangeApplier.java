package com.atlas.app.improvement;

public interface ImprovementLabChangeApplier {

    boolean apply(
            ImprovementChangeProposal proposal,
            ImprovementLabWorkspace workspace
    );
}
