package com.atlas.app.improvement;

public class ImprovementLabEvaluator {

    public ImprovementLabEvaluationResult evaluate(
            ImprovementLabWorkspace workspace,
            ImprovementLabBuildResult buildResult
    ) {
        if (workspace == null) {
            return failed(
                    "LAB workspace is missing",
                    "",
                    ""
            );
        }

        if (buildResult == null) {
            workspace.markFailed();

            return failed(
                    "LAB build result is missing",
                    "",
                    ""
            );
        }

        if (!buildResult.isBuildSuccess()) {
            workspace.markFailed();

            return failed(
                    "LAB build failed",
                    buildResult.getBuildOutput(),
                    buildResult.getTestOutput()
            );
        }

        if (!buildResult.isTestSuccess()) {
            workspace.markFailed();

            return failed(
                    "LAB tests failed",
                    buildResult.getBuildOutput(),
                    buildResult.getTestOutput()
            );
        }

        workspace.markChanged();
        workspace.markValidated();

        return new ImprovementLabEvaluationResult(
                true,
                ImprovementLabStatus.VALIDATED,
                "LAB build and tests passed",
                buildResult.getBuildOutput(),
                buildResult.getTestOutput()
        );
    }

    private ImprovementLabEvaluationResult failed(
            String reason,
            String buildOutput,
            String testOutput
    ) {
        return new ImprovementLabEvaluationResult(
                false,
                ImprovementLabStatus.FAILED,
                reason,
                buildOutput,
                testOutput
        );
    }
}
