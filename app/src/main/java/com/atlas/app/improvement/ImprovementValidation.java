package com.atlas.app.improvement;

public class ImprovementValidation {

    private final String criteria;
    private final String expectedResult;

    public ImprovementValidation(
            String criteria,
            String expectedResult
    ) {
        this.criteria = criteria;
        this.expectedResult = expectedResult;
    }

    public String getCriteria() {
        return criteria;
    }

    public String getExpectedResult() {
        return expectedResult;
    }
}
