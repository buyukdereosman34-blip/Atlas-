package com.atlas.app.improvement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImprovementValidationTest {

    @Test
    public void validationStoresCriteriaAndExpectedResult() {
        ImprovementValidation validation =
                new ImprovementValidation(
                        "Run unit tests",
                        "All tests pass"
                );

        assertEquals(
                "Run unit tests",
                validation.getCriteria()
        );

        assertEquals(
                "All tests pass",
                validation.getExpectedResult()
        );
    }
}
