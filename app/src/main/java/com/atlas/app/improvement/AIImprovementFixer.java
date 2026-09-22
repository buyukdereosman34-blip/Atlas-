package com.atlas.app.improvement;

import com.atlas.app.ai.AIProviderRouter;

import java.util.Collections;

public class AIImprovementFixer implements ImprovementFixer {

    private final ImprovementChangeProposalGenerator generator;

    public AIImprovementFixer(
            AIProviderRouter router
    ) {
        this.generator =
                new ImprovementChangeProposalGenerator(router);
    }

    @Override
    public ImprovementChangeProposal fix(
            ImprovementTask task,
            ImprovementFinding finding,
            ImprovementChangeProposal failedProposal,
            ImprovementLabBuildResult buildResult,
            ImprovementLabEvaluationResult evaluationResult
    ) {
        if (task == null || finding == null || buildResult == null) {
            return null;
        }

        StringBuilder failureContext =
                new StringBuilder();

        failureContext
                .append("The previous generated change failed validation.\n\n")
                .append("Build success: ")
                .append(buildResult.isBuildSuccess())
                .append('\n')
                .append("Test success: ")
                .append(buildResult.isTestSuccess())
                .append('\n')
                .append("Build exit code: ")
                .append(buildResult.getBuildExitCode())
                .append('\n')
                .append("Test exit code: ")
                .append(buildResult.getTestExitCode())
                .append("\n\n");

        if (buildResult.getBuildOutput() != null) {
            failureContext
                    .append("Build output:\n")
                    .append(buildResult.getBuildOutput())
                    .append("\n\n");
        }

        if (buildResult.getTestOutput() != null) {
            failureContext
                    .append("Test output:\n")
                    .append(buildResult.getTestOutput())
                    .append("\n\n");
        }

        if (evaluationResult != null) {
            failureContext
                    .append("Evaluation:\n")
                    .append(evaluationResult.getReason())
                    .append('\n');
        }

        String problem =
                finding.getProblem()
                        + "\n\n"
                        + failureContext;

        String recommendation =
                "Fix the previous generated change so that the project "
                        + "builds and all tests pass. "
                        + "Preserve the original task and change only what "
                        + "is necessary to correct the failure.";

        ImprovementFinding fixFinding =
                new ImprovementFinding(
                        finding.getArea(),
                        problem,
                        recommendation
                );

        return generator.generate(
                task,
                fixFinding,
                failedProposal == null
                        ? Collections.emptyList()
                        : failedProposal.getAffectedFiles(),
                null
        );
    }
}
