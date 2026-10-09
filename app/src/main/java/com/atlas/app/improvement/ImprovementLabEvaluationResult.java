package com.atlas.app.improvement;

public class ImprovementLabEvaluationResult {

    private final boolean passed;
    private final ImprovementLabStatus status;
    private final String reason;
    private final String buildOutput;
    private final String testOutput;
    private final boolean partial;

    public ImprovementLabEvaluationResult(
            boolean passed,
            ImprovementLabStatus status,
            String reason,
            String buildOutput,
            String testOutput
    ) {
        this(passed, status, reason, buildOutput, testOutput, false);
    }

    public ImprovementLabEvaluationResult(
            boolean passed,
            ImprovementLabStatus status,
            String reason,
            String buildOutput,
            String testOutput,
            boolean partial
    ) {
        this.passed = passed;
        this.status = status;
        this.reason = reason;
        this.buildOutput = buildOutput;
        this.testOutput = testOutput;
        this.partial = partial;
    }

    public boolean isPassed() {
        return passed;
    }

    public ImprovementLabStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public String getBuildOutput() {
        return buildOutput;
    }

    public String getTestOutput() {
        return testOutput;
    }

    public boolean isPartial() {
        return partial;
    }
}
