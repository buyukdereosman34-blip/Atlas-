package com.atlas.app.improvement;

public final class ImprovementStuckDetector {

    public boolean isStuck(
            ImprovementFailureSignature previous,
            ImprovementFailureSignature current,
            int consecutiveCount,
            ImprovementFailureCategory category
    ) {
        if (previous == null || current == null) {
            return false;
        }

        if (!previous.equals(current)) {
            return false;
        }

        int limit = (category == ImprovementFailureCategory.CODE) ? 3 : 2;
        return consecutiveCount >= limit;
    }

    // Eski cagrilari bozmamak icin (geriye uyumlu)
    public boolean isStuck(
            ImprovementFailureSignature previous,
            ImprovementFailureSignature current
    ) {
        return isStuck(previous, current, 2, ImprovementFailureCategory.UNKNOWN);
    }
}
