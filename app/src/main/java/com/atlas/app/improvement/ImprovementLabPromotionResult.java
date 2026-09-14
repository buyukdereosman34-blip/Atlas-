package com.atlas.app.improvement;

public class ImprovementLabPromotionResult {

    private final boolean success;
    private final String reason;
    private final String sourcePath;
    private final String targetPath;

    public ImprovementLabPromotionResult(
            boolean success,
            String reason,
            String sourcePath,
            String targetPath
    ) {
        this.success = success;
        this.reason = reason;
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getReason() {
        return reason;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getTargetPath() {
        return targetPath;
    }
}
