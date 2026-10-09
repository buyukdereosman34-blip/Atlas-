package com.atlas.app.improvement;

public final class ImprovementStuckDetector {

    public boolean isStuck(ImprovementFailureSignature previous,
                           ImprovementFailureSignature current) {
        if (previous == null || current == null) {
            return false;
        }
        return previous.equals(current);
    }
}
