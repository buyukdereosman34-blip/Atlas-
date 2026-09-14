package com.atlas.app.improvement;

public class ImprovementMethod {

    private final String name;
    private final String description;
    private final String source;
    private final String requirements;
    private final String risk;
    private int successCount;
    private int failureCount;

    public ImprovementMethod(
            String name,
            String description,
            String source,
            String requirements,
            String risk
    ) {
        this.name = name;
        this.description = description;
        this.source = source;
        this.requirements = requirements;
        this.risk = risk;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }

    public String getRequirements() {
        return requirements;
    }

    public String getRisk() {
        return risk;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void recordSuccess() {
        successCount++;
    }

    public void recordFailure() {
        failureCount++;
    }

    public int getScore() {
        return successCount - failureCount;
    }
}
