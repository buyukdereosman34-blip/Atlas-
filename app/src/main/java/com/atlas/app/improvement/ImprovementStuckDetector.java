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

        // CODE: 4 build denemesi = 3 fixer hakki. Digerleri: 2 build = 1 fixer.
        int limit = (category == ImprovementFailureCategory.CODE) ? 4 : 2;
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
