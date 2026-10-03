package com.atlas.app.improvement;

public enum ImprovementFailureCategory {

    CODE,
    TEST,
    BUILD,
    ENVIRONMENT,
    TOOLCHAIN,
    PERMISSION,
    DEPENDENCY,
    UNKNOWN;

    public boolean isRetryable() {
        return this == CODE
                || this == TEST
                || this == BUILD;
    }

    public boolean isEnvironmentIssue() {
        return this == ENVIRONMENT
                || this == TOOLCHAIN
                || this == PERMISSION
                || this == DEPENDENCY;
    }
}
