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

        return new ImprovementProposal(
                "Investigate: " + observation.getMessage(),
                "Observation from " + observation.getSource()
                        + " with severity " + observation.getSeverity(),
                "Investigate the observed problem and identify a minimal improvement.",
                "The proposed improvement must be validated with a reproducible test."
        );
    }
}
