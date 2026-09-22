package com.atlas.app.improvement;

public interface ImprovementFixer {

    ImprovementChangeProposal fix(
            ImprovementTask task,
            ImprovementFinding finding,
            ImprovementChangeProposal failedProposal,
            ImprovementLabBuildResult buildResult,
            ImprovementLabEvaluationResult evaluationResult
    );
}
