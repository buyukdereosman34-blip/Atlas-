package com.atlas.app.improvement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelfImprovementEngine implements ImprovementObserver {

    private final List<ImprovementObservation> observations =
            new ArrayList<>();

    private final ImprovementMethodStore methodStore;
    private final ImprovementTaskAnalyzer taskAnalyzer;
    private final ImprovementMethodRunner methodRunner;

    public SelfImprovementEngine() {
        this(new ImprovementMethodStore());
    }

    public SelfImprovementEngine(
            ImprovementMethodStore methodStore
    ) {
        this.methodStore = methodStore;
        this.taskAnalyzer =
                new ImprovementTaskAnalyzer(methodStore);
        this.methodRunner =
                new ImprovementMethodRunner(
                        new ImprovementMethodRanker()
                );
    }

    public void observe(ImprovementObservation observation) {
        if (observation == null) {
            return;
        }

        observations.add(observation);
    }

    @Override
    public void onObservation(
            ImprovementObservation observation
    ) {
        observe(observation);
    }

    public List<ImprovementObservation> getObservations() {
        return Collections.unmodifiableList(observations);
    }

    public ImprovementProposal analyze(
            ImprovementObservation observation
    ) {
        if (observation == null) {
            return null;
        }

        ImprovementValidation validation =
                new ImprovementValidation(
                        "Reproduce and test the observed problem",
                        "The problem is reproducible and the proposed improvement passes validation"
                );

        return new ImprovementProposal(
                "Investigate: " + observation.getMessage(),
                "Observation from " + observation.getSource()
                        + " with severity " + observation.getSeverity(),
                "Investigate the observed problem and identify a minimal improvement.",
                validation
        );
    }

    public ImprovementFinding analyzeTask(
            ImprovementTask task
    ) {
        return taskAnalyzer.analyze(task);
    }

    public ImprovementMethod executeTask(
            ImprovementTask task,
            ImprovementMethodExecutor executor
    ) {
        if (task == null) {
            return null;
        }

        if (executor == null) {
            return null;
        }

        return methodRunner.run(
                methodStore.getAll(),
                executor
        );
    }

    public void addMethod(
            ImprovementMethod method
    ) {
        methodStore.add(method);
    }

    public ImprovementMethodStore getMethodStore() {
        return methodStore;
    }
}
