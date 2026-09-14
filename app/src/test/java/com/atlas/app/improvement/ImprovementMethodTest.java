package com.atlas.app.improvement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImprovementMethodTest {

    @Test
    public void recordsSuccessAndFailure() {
        ImprovementMethod method =
                new ImprovementMethod(
                        "Test",
                        "Test method",
                        "runtime",
                        "none",
                        "LOW"
                );

        method.recordSuccess();
        method.recordSuccess();
        method.recordFailure();

        assertEquals(2, method.getSuccessCount());
        assertEquals(1, method.getFailureCount());
        assertEquals(1, method.getScore());
    }
}
