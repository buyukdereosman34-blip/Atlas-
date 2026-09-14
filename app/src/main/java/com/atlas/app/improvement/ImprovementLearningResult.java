package com.atlas.app.improvement;

public class ImprovementLearningResult {

    private final boolean recorded;
    private final String methodName;
    private final boolean success;
    private final int score;
    private final String message;

    public ImprovementLearningResult(
            boolean recorded,
            String methodName,
            boolean success,
            int score,
            String message
    ) {
        this.recorded = recorded;
        this.methodName = methodName;
        this.success = success;
        this.score = score;
        this.message = message;
    }

    public boolean isRecorded() {
        return recorded;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getScore() {
        return score;
    }

    public String getMessage() {
        return message;
    }
}
