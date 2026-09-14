package com.atlas.app.improvement;

public class ImprovementLabBuildResult {

    private final boolean buildSuccess;
    private final boolean testSuccess;
    private final int buildExitCode;
    private final int testExitCode;
    private final String buildOutput;
    private final String testOutput;

    public ImprovementLabBuildResult(
            boolean buildSuccess,
            boolean testSuccess,
            int buildExitCode,
            int testExitCode,
            String buildOutput,
            String testOutput
    ) {
        this.buildSuccess = buildSuccess;
        this.testSuccess = testSuccess;
        this.buildExitCode = buildExitCode;
        this.testExitCode = testExitCode;
        this.buildOutput = buildOutput;
        this.testOutput = testOutput;
    }

    public boolean isBuildSuccess() {
        return buildSuccess;
    }

    public boolean isTestSuccess() {
        return testSuccess;
    }

    public int getBuildExitCode() {
        return buildExitCode;
    }

    public int getTestExitCode() {
        return testExitCode;
    }

    public String getBuildOutput() {
        return buildOutput;
    }

    public String getTestOutput() {
        return testOutput;
    }

    public boolean isSuccessful() {
        return buildSuccess && testSuccess;
    }
}
