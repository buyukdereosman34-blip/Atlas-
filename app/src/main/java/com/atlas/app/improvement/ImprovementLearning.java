package com.atlas.app.improvement;

public class ImprovementLearning {

    private final ImprovementMethodStore methodStore;

    public ImprovementLearning(
            ImprovementMethodStore methodStore
    ) {
        this.methodStore = methodStore;
    }

    public ImprovementLearningResult record(
            ImprovementMethod method,
            boolean success
    ) {
        if (method == null) {
            return new ImprovementLearningResult(
                    false,
                    "",
                    success,
                    0,
                    "Improvement method is missing"
            );
        }

        if (success) {
            method.recordSuccess();
        } else {
            method.recordFailure();
        }

        return new ImprovementLearningResult(
                true,
                method.getName(),
                success,
                method.getScore(),
                success
                        ? "Improvement method success recorded"
                        : "Improvement method failure recorded"
        );
    }

    public ImprovementMethod findBestMethod() {
        return methodStore.findBest();
    }

    public ImprovementMethodStore getMethodStore() {
        return methodStore;
    }
}
