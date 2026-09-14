package com.atlas.app.improvement;

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

    public ImprovementSelfDevelopmentLoop(
            ImprovementMethodStore methodStore
    ) {
        this.ranker = new ImprovementMethodRanker();
        this.proposalGenerator =
                new ImprovementChangeProposalGenerator();
        this.changeSafety =
                new ImprovementChangeSafety();
        this.labManager =
                new ImprovementLabManager();
        this.buildExecutor =
                new ImprovementLabBuildExecutor();
        this.evaluator =
                new ImprovementLabEvaluator();
        this.promoter =
                new ImprovementLabPromoter();
        this.learning =
                new ImprovementLearning(methodStore);
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
                        affectedFiles
                );

        ImprovementChangeSafetyReport safetyReport =
                changeSafety.evaluate(proposal);

        if (!safetyReport.isAllowed()) {
            return new ImprovementSelfDevelopmentResult(
                    false,
                    "SAFETY",
                    safetyReport.getReason(),
                    null,
                    proposal,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        ImprovementLabWorkspace workspace =
                labManager.createWorkspace(
                        proposal,
                        sourcePath,
                        labRoot
                );

        if (workspace == null) {
            return new ImprovementSelfDevelopmentResult(
                    false,
                    "LAB",
                    "Unable to create LAB workspace",
                    null,
                    proposal,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        ImprovementMethod method =
                selectMethod();

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
                    learning.record(
                            method,
                            false
                    );

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

        ImprovementLabBuildResult buildResult =
                buildExecutor.buildAndTest(workspace);

        ImprovementLabEvaluationResult evaluationResult =
                evaluator.evaluate(
                        workspace,
                        buildResult
                );

        if (!evaluationResult.isPassed()) {
            labManager.archiveFailedWorkspace(
                    workspace,
                    labRoot
            );

            ImprovementLearningResult learningResult =
                    learning.record(
                            method,
                            false
                    );

            return new ImprovementSelfDevelopmentResult(
                    false,
                    "EVALUATION",
                    evaluationResult.getReason(),
                    method,
                    proposal,
                    workspace,
                    buildResult,
                    evaluationResult,
                    null,
                    learningResult
            );
        }

        ImprovementLabPromotionResult promotionResult =
                promoter.promote(
                        workspace,
                        targetPath
                );

        if (!promotionResult.isSuccess()) {
            ImprovementLearningResult learningResult =
                    learning.record(
                            method,
                            false
                    );

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

        ImprovementLearningResult learningResult =
                learning.record(
                        method,
                        true
                );

        return new ImprovementSelfDevelopmentResult(
                true,
                "COMPLETED",
                "Self-development loop completed successfully",
                method,
                proposal,
                workspace,
                buildResult,
                evaluationResult,
                promotionResult,
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
