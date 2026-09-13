package com.atlas.app.improvement;

public class ImprovementObservationCollector {

    public ImprovementObservation collect(
            String source,
            String message,
            String severity
    ) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        if (message == null || message.trim().isEmpty()) {
            return null;
        }

        if (severity == null || severity.trim().isEmpty()) {
            return null;
        }

        return new ImprovementObservation(
                source.trim(),
                message.trim(),
                severity.trim()
        );
    }
}
