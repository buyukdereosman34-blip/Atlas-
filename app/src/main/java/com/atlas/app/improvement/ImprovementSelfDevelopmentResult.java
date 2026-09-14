package com.atlas.app.improvement;

public class ImprovementSelfDevelopmentResult {

    private final boolean success;
    private final String stage;
    private final String message;
    private final ImprovementMethod method;
    private final ImprovementChangeProposal proposal;
    private final ImprovementLabWorkspace workspace;
    private final ImprovementLabBuildResult buildResult;
    private final ImprovementLabEvaluationResult evaluationResult;
    private final ImprovementLabPromotionResult promotionResult;
    private final ImprovementLearningResult learningResult;

    public ImprovementSelfDevelopmentResult(
            boolean success,
            String stage,
            String message,
            ImprovementMethod method,
            ImprovementChangeProposal proposal,
            ImprovementLabWorkspace workspace,
            ImprovementLabBuildResult buildResult,
            ImprovementLabEvaluationResult evaluationResult,
            ImprovementLabPromotionResult promotionResult,
            ImprovementLearningResult learningResult
    ) {
        this.success = success;
        this.stage = stage;
        this.message = message;
        this.method = method;
        this.proposal = proposal;
        this.workspace = workspace;
        this.buildResult = buildResult;
        this.evaluationResult = evaluationResult;
        this.promotionResult = promotionResult;
        this.learningResult = learningResult;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getStage() {
        return stage;
    }

    public String getMessage() {
        return message;
    }

    public ImprovementMethod getMethod() {
        return method;
    }

    public ImprovementChangeProposal getProposal() {
        return proposal;
    }

    public ImprovementLabWorkspace getWorkspace() {
        return workspace;
    }

    public ImprovementLabBuildResult getBuildResult() {
        return buildResult;
    }

    public ImprovementLabEvaluationResult getEvaluationResult() {
        return evaluationResult;
    }

    public ImprovementLabPromotionResult getPromotionResult() {
        return promotionResult;
    }

    public ImprovementLearningResult getLearningResult() {
        return learningResult;
    }
}
