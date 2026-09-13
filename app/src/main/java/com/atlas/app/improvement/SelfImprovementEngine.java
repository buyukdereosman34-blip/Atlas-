package com.atlas.app.improvement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelfImprovementEngine {

    private final List<ImprovementObservation> observations =
            new ArrayList<>();

    public void observe(ImprovementObservation observation) {
        if (observation == null) {
            return;
        }

        observations.add(observation);
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
}
