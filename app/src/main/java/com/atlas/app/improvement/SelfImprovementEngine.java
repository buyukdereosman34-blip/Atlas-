package com.atlas.app.improvement;

import com.atlas.app.ai.AIProviderRouter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelfImprovementEngine implements ImprovementObserver {

    private final List<ImprovementObservation> observations =
            new ArrayList<>();

    private final ImprovementMethodStore methodStore;
    private final ImprovementTaskAnalyzer taskAnalyzer;
    private final ImprovementMethodRunner methodRunner;
    private final ImprovementChangeProposalGenerator changeProposalGenerator;
    private final ImprovementChangeSafety changeSafety;
    private final ImprovementSelfDevelopmentLoop selfDevelopmentLoop;

    public SelfImprovementEngine() {
        this(new ImprovementMethodStore());
    }

    public SelfImprovementEngine(
            ImprovementMethodStore methodStore
    ) {
        this(
                methodStore,
                null
        );
    }

    public SelfImprovementEngine(
            ImprovementMethodStore methodStore,
            AIProviderRouter router
    ) {
        this.methodStore = methodStore;
        this.taskAnalyzer =
                new ImprovementTaskAnalyzer(methodStore);
        this.methodRunner =
                new ImprovementMethodRunner(
                        new ImprovementMethodRanker()
                );

        if (router != null) {
            this.changeProposalGenerator =
                    new ImprovementChangeProposalGenerator(
                            router
                    );

            this.selfDevelopmentLoop =
                    new ImprovementSelfDevelopmentLoop(
                            methodStore,
                            router
                    );
        } else {
            this.changeProposalGenerator =
                    new ImprovementChangeProposalGenerator();

            this.selfDevelopmentLoop =
                    new ImprovementSelfDevelopmentLoop(
                            methodStore
                    );
        }

        this.changeSafety =
                new ImprovementChangeSafety();
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

    public ImprovementSelfDevelopmentResult executeSelfDevelopment(
            ImprovementTask task,
            List<String> affectedFiles,
            String sourcePath,
            String labRoot,
            String targetPath,
            ImprovementLabChangeApplier changeApplier
    ) {
        if (task == null) {
            return new ImprovementSelfDevelopmentResult(
                    false,
                    "INPUT",
                    "Improvement task is missing",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        ImprovementFinding finding =
                analyzeTask(task);

        if (finding == null) {
            return new ImprovementSelfDevelopmentResult(
                    false,
                    "ANALYSIS",
                    "Unable to analyze improvement task",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        return selfDevelopmentLoop.execute(
                task,
                finding,
                affectedFiles,
                sourcePath,
                labRoot,
                targetPath,
                changeApplier
        );
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

    public ImprovementChangeProposal proposeChange(
            ImprovementTask task,
            ImprovementFinding finding,
            List<String> affectedFiles
    ) {
        return changeProposalGenerator.generate(
                task,
                finding,
                affectedFiles
        );
    }

    public ImprovementChangeSafetyReport evaluateChangeSafety(
            ImprovementChangeProposal proposal
    ) {
        return changeSafety.evaluate(proposal);
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
