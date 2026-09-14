package com.atlas.app.improvement;

public class ImprovementTaskAnalyzer {

    private final ImprovementMethodStore methodStore;

    public ImprovementTaskAnalyzer(
            ImprovementMethodStore methodStore
    ) {
        this.methodStore = methodStore;
    }

    public ImprovementFinding analyze(ImprovementTask task) {
        if (task == null) {
            return null;
        }

        ImprovementMethod method = methodStore.findBest();

        String recommendation;

        if (method != null) {
            recommendation =
                    "Use method: " + method.getName();
        } else {
            recommendation =
                    "Inspect the ATLAS codebase and identify a suitable method.";
        }

        return new ImprovementFinding(
                "ATLAS",
                task.getDescription(),
                recommendation
        );
    }
}
