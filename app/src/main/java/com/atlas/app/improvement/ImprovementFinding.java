package com.atlas.app.improvement;

public class ImprovementFinding {

    private final String area;
    private final String problem;
    private final String recommendation;

    public ImprovementFinding(
            String area,
            String problem,
            String recommendation
    ) {
        this.area = area;
        this.problem = problem;
        this.recommendation = recommendation;
    }

    public String getArea() {
        return area;
    }

    public String getProblem() {
        return problem;
    }

    public String getRecommendation() {
        return recommendation;
    }
}
