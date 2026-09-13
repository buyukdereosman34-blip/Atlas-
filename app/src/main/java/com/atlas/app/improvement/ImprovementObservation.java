package com.atlas.app.improvement;

public class ImprovementObservation {

    private final String source;
    private final String message;
    private final String severity;

    public ImprovementObservation(
            String source,
            String message,
            String severity
    ) {
        this.source = source;
        this.message = message;
        this.severity = severity;
    }

    public String getSource() {
        return source;
    }

    public String getMessage() {
        return message;
    }

    public String getSeverity() {
        return severity;
    }
}
