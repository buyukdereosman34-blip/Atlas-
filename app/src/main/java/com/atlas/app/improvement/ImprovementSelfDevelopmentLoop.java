package com.atlas.app.improvement;

import com.atlas.app.ai.AIProviderRouter;

import java.util.List;

public class ImprovementSelfDevelopmentLoop {

    private final ImprovementMethodRanker ranker;
    private final ImprovementChangeProposalGenerator proposalGenerator;
    private final ImprovementChangeSafety changeSafety;
    private final ImprovementLabManager labManager;
    private final ImprovementLabBuildExecutor buildExecutor;
    private final ImprovementLabEvaluator evaluator;
    private final ImprovementLabPromoter promoter;
    private final ImprovementLearning learning;
    private final ImprovementFixer fixer;
    private final ImprovementStuckDetector stuckDetector;
    
    public interface ApprovalCallback {
        boolean requestApproval(ImprovementChangeProposal proposal, String diffSummary);
    }
    
    private ApprovalCallback approvalCallback = (p, d) -> true;

    public void setApprovalCallback(ApprovalCallback callback) {
        this.approvalCallback = callback != null ? callback : (p, d) -> true;
    }
    private final ImprovementFailureClassifier classifier;

    public ImprovementSelfDevelopmentLoop(
            ImprovementMethodStore methodStore
    ) {
        this(
                methodStore,
                null,
                null
        );
    }

    public ImprovementSelfDevelopmentLoop(
            ImprovementMethodStore methodStore,
            AIProviderRouter aiProviderRouter
    ) {
        this(
                methodStore,
                aiProviderRouter,
                null
        );
    }

    public ImprovementSelfDevelopmentLoop(
            ImprovementMethodStore methodStore,
            AIProviderRouter aiProviderRouter,
            ImprovementFixer fixer
    ) {
        this(
                methodStore,
                aiProviderRouter,
                fixer,
                null
        );
    }

    public ImprovementSelfDevelopmentLoop(
            ImprovementMethodStore methodStore,
            AIProviderRouter aiProviderRouter,
            ImprovementFixer fixer,
            ImprovementLabBuildExecutor buildExecutorOverride
    ) {
        this.ranker = new ImprovementMethodRanker();

        this.proposalGenerator =
                aiProviderRouter == null
                        ? new ImprovementChangeProposalGenerator()
                        : new ImprovementChangeProposalGenerator(
                                aiProviderRouter
                        );

        this.changeSafety =
                new ImprovementChangeSafety();
        this.labManager =
                new ImprovementLabManager();
        this.buildExecutor =
                buildExecutorOverride != null
                        ? buildExecutorOverride
                        : new ImprovementLabBuildExecutor();
        this.evaluator =
                new ImprovementLabEvaluator();
        this.promoter =
                new ImprovementLabPromoter();
        this.learning =
                new ImprovementLearning(methodStore);
        this.stuckDetector =
                new ImprovementStuckDetector();
        this.classifier =
                new ImprovementFailureClassifier();
        this.fixer =
                fixer != null
                        ? fixer
                        : aiProviderRouter == null
                                ? null
                                : new AIImprovementFixer(
                                        aiProviderRouter
                                );
    }

    public ImprovementSelfDevelopmentResult execute(
            ImprovementTask task,
            ImprovementFinding finding,
            List<String> affectedFiles,
            String sourcePath,
            String labRoot,
            String targetPath,
            ImprovementLabChangeApplier changeApplier
    ) {
        if (task == null) {
            return failure(
                    "INPUT",
                    "Improvement task is missing"
            );
        }

        if (finding == null) {
            return failure(
                    "ANALYSIS",
                    "Improvement finding is missing"
            );
        }

        if (changeApplier == null) {
            return failure(
                    "CHANGE",
                    "LAB change applier is missing"
            );
        }

        ImprovementChangeProposal proposal =
                proposalGenerator.generate(
                        task,
                        finding,
                        affectedFiles,
                        sourcePath
                );

        ImprovementMethod method =
                selectMethod();

        ImprovementLabWorkspace lastWorkspace = null;
        ImprovementLabBuildResult lastBuildResult = null;
        ImprovementLabEvaluationResult lastEvaluationResult = null;

        // === STATE MACHINE BASLANGICI ===
        DevelopmentState currentState = DevelopmentState.PROPOSED;
        currentState = DevelopmentState.PENDING_APPROVAL;
        
        boolean userApproved = approvalCallback.requestApproval(proposal, "");
        if (!userApproved) {
            currentState = DevelopmentState.REJECTED;
            return failure("APPROVAL", "Development proposal rejected by user");
        }
        currentState = DevelopmentState.APPROVED;

        final int maxAttempts = 4;

        ImprovementFailureSignature previousSignature = null;
        int consecutiveCount = 0;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {

            if (proposal == null) {
                ImprovementLearningResult learningResult =
                        learning.record(method, false);

                return new ImprovementSelfDevelopmentResult(
                        false,
                        "GENERATION",
                        "Unable to generate improvement proposal",
                        method,
                        null,
                        lastWorkspace,
                        lastBuildResult,
                        lastEvaluationResult,
                        null,
                        learningResult
                );
            }

            ImprovementChangeSafetyReport safetyReport =
                    changeSafety.evaluate(proposal);

            if (!safetyReport.isAllowed()) {
                ImprovementLearningResult learningResult =
                        learning.record(method, false);

                return new ImprovementSelfDevelopmentResult(
                        false,
                        "SAFETY",
                        safetyReport.getReason(),
                        method,
                        proposal,
                        lastWorkspace,
                        lastBuildResult,
                        lastEvaluationResult,
                        null,
                        learningResult
                );
            }

            ImprovementLabWorkspace workspace =
                    labManager.createWorkspace(
                            proposal,
                            sourcePath,
                            labRoot
                    );

            if (workspace == null) {
                ImprovementLearningResult learningResult =
                        learning.record(method, false);

                return new ImprovementSelfDevelopmentResult(
                        false,
                        "LAB",
                        "Unable to create LAB workspace",
                        method,
                        proposal,
                        null,
                        lastBuildResult,
                        lastEvaluationResult,
                        null,
                        learningResult
                );
            }

            lastWorkspace = workspace;

            if (!changeApplier.apply(
                    proposal,
                    workspace
            )) {
                workspace.markFailed();

                labManager.archiveFailedWorkspace(
                        workspace,
                        labRoot
                );

                ImprovementLearningResult learningResult =
                        learning.record(method, false);

                return new ImprovementSelfDevelopmentResult(
                        false,
                        "CHANGE",
                        "LAB change application failed",
                        method,
                        proposal,
                        workspace,
                        null,
                        null,
                        null,
                        learningResult
                );
            }

            workspace.markChanged();
            currentState = DevelopmentState.APPLYING;

            ImprovementLabBuildResult buildResult =
                    buildExecutor.buildAndTest(workspace);

            currentState = DevelopmentState.VALIDATING;
            
            ImprovementLabEvaluationResult evaluationResult =
                    evaluator.evaluate(
                            workspace,
                            buildResult
                    );

            lastBuildResult = buildResult;
            lastEvaluationResult = evaluationResult;

            if (evaluationResult.isPartial()) {
                ImprovementLearningResult learningResult =
                        learning.record(method, false);

                return new ImprovementSelfDevelopmentResult(
                        false,
                        "PARTIAL",
                        evaluationResult.getReason(),
                        method,
                        proposal,
                        workspace,
                        buildResult,
                        evaluationResult,
                        null,
                        learningResult,
                        new ImprovementFailureClassification(
                                ImprovementFailureCategory.TOOLCHAIN,
                                "AAPT2 toolchain failure - partial validation only",
                                ""
                        )
                );
            }

            if (evaluationResult.isPassed()) {

                ImprovementLabPromotionResult promotionResult =
                        promoter.promote(
                                workspace,
                                targetPath
                        );

                if (!promotionResult.isSuccess()) {
                    ImprovementLearningResult learningResult =
                            learning.record(method, false);

                    return new ImprovementSelfDevelopmentResult(
                            false,
                            "PROMOTION",
                            promotionResult.getReason(),
                            method,
                            proposal,
                            workspace,
                            buildResult,
                            evaluationResult,
                            promotionResult,
                            learningResult
                    );
                }

                currentState = DevelopmentState.PROMOTED;

                ImprovementLearningResult learningResult =
                        learning.record(method, true);

                return new ImprovementSelfDevelopmentResult(
                        true,
                        "COMPLETED",
                        "Self-development loop completed successfully "
                                + "on attempt " + attempt,
                        method,
                        proposal,
                        workspace,
                        buildResult,
                        evaluationResult,
                        promotionResult,
                        learningResult
                );
            }

            workspace.markFailed();

            labManager.archiveFailedWorkspace(
                    workspace,
                    labRoot
            );

            ImprovementFailureClassification classification =
                    classifier.classify(buildResult);

            if (classification == null) {
                classification = new ImprovementFailureClassification(
                        ImprovementFailureCategory.UNKNOWN,
                        "Classifier returned no result",
                        ""
                );
            }

            if (classification.isEnvironmentIssue()) {
                ImprovementLearningResult learningResult =
                        learning.record(method, false);

                return new ImprovementSelfDevelopmentResult(
                        false,
                        "EVALUATION",
                        "Environment/toolchain failure ["
                                + classification.getCategory()
                                + "] after "
                                + attempt
                                + " attempt(s): "
                                + classification.getReason()
                                + " - "
                                + evaluationResult.getReason(),
                        method,
                        proposal,
                        workspace,
                        buildResult,
                        evaluationResult,
                        null,
                        learningResult,
                        classification
                );
            }

            ImprovementFailureSignature currentSignature =
                    ImprovementFailureSignature.from(classification);

            // Sayaç güncelle: aynı imza mı, yeni mi?
            if (previousSignature != null
                    && previousSignature.equals(currentSignature)) {
                consecutiveCount++;
            } else {
                consecutiveCount = 1;
            }

            // Stuck mı? (kategori bazlı limit: CODE=3, diğerleri=2)
            boolean isStuck = stuckDetector.isStuck(
                    previousSignature,
                    currentSignature,
                    consecutiveCount,
                    classification.getCategory()
            );

            // ÖNCE fixer hakkı ver (ama stuck değilse ve deneme hakkı varsa)
            if (attempt < maxAttempts
                    && fixer != null
                    && classification.isRetryable()
                    && !isStuck) {

                proposal =
                        fixer.fix(
                                task,
                                finding,
                                proposal,
                                buildResult,
                                evaluationResult
                        );

                previousSignature = currentSignature;
                continue;
            }

            // Fixer hakkı bitti veya stuck → STUCK dön
            if (isStuck) {
                ImprovementLearningResult learningResult =
                        learning.record(method, false);

                return new ImprovementSelfDevelopmentResult(
                        false,
                        "STUCK",
                        "[STUCK] Same failure repeated "
                                + consecutiveCount
                                + " times (limit: "
                                + (classification.getCategory()
                                        == ImprovementFailureCategory.CODE ? 4 : 2)
                                + "): "
                                + currentSignature,
                        method,
                        proposal,
                        workspace,
                        buildResult,
                        evaluationResult,
                        null,
                        learningResult,
                        classification
                );
            }

            previousSignature = currentSignature;

            ImprovementLearningResult learningResult =
                    learning.record(method, false);

            return new ImprovementSelfDevelopmentResult(
                    false,
                    "EVALUATION",
                    "Self-development failed after "
                            + attempt
                            + " attempt(s) ["
                            + classification.getCategory()
                            + "]: "
                            + evaluationResult.getReason(),
                    method,
                    proposal,
                    workspace,
                    buildResult,
                    evaluationResult,
                    null,
                    learningResult,
                    classification
            );
        }

        ImprovementLearningResult learningResult =
                learning.record(method, false);

        return new ImprovementSelfDevelopmentResult(
                false,
                "RETRY",
                "Self-development retry loop ended unexpectedly",
                method,
                proposal,
                lastWorkspace,
                lastBuildResult,
                lastEvaluationResult,
                null,
                learningResult
        );
    }

    private ImprovementMethod selectMethod() {
        List<ImprovementMethod> methods =
                learningMethods();

        return ranker.best(methods);
    }

    private List<ImprovementMethod> learningMethods() {
        return learningStore().getAll();
    }

    private ImprovementMethodStore learningStore() {
        return learning.getMethodStore();
    }

    private ImprovementSelfDevelopmentResult failure(
            String stage,
            String message
    ) {
        return new ImprovementSelfDevelopmentResult(
                false,
                stage,
                message,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

}
