package com.atlas.app.improvement;

import java.util.Locale;

public final class ImprovementFailureSignature {

    private final ImprovementFailureCategory category;
    private final String pattern;

    private ImprovementFailureSignature(ImprovementFailureCategory category, String pattern) {
        this.category = category == null ? ImprovementFailureCategory.UNKNOWN : category;
        this.pattern = pattern == null ? "" : pattern;
    }

    public static ImprovementFailureSignature from(ImprovementFailureClassification classification) {
        if (classification == null) {
            return new ImprovementFailureSignature(ImprovementFailureCategory.UNKNOWN, "");
        }
        return new ImprovementFailureSignature(
                classification.getCategory(),
                normalize(classification.getReason())
        );
    }

    static String normalize(String raw) {
        if (raw == null) return "";
        String s = raw.toLowerCase(Locale.ROOT).trim();
        s = s.replaceAll("\\s+", " ");
        s = s.replaceAll("\\b\\d+\\b", "");
        s = s.replaceAll("\\s+", " ").trim();
        return s.length() > 120 ? s.substring(0, 120) : s;
    }

    public ImprovementFailureCategory getCategory() {
        return category;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImprovementFailureSignature)) return false;
        ImprovementFailureSignature other = (ImprovementFailureSignature) o;
        return category == other.category && pattern.equals(other.pattern);
    }

    @Override
    public int hashCode() {
        return 31 * category.hashCode() + pattern.hashCode();
    }

    @Override
    public String toString() {
        return category + " - " + pattern;
    }
}
