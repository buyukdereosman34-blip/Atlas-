package com.atlas.app.improvement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImprovementLearningTest {

    @Test
    public void recordsSuccessfulMethod() {
        ImprovementMethodStore store =
                new ImprovementMethodStore();

        ImprovementMethod method =
                new ImprovementMethod(
                        "method-a",
                        "Test method A",
                        "test",
                        "none",
                        "LOW"
                );

        store.add(method);

        ImprovementLearning learning =
                new ImprovementLearning(store);

        ImprovementLearningResult result =
                learning.record(method, true);

        assertTrue(result.isRecorded());
        assertTrue(result.isSuccess());
        assertEquals("method-a", result.getMethodName());
        assertEquals(1, result.getScore());
        assertEquals(1, method.getSuccessCount());
        assertEquals(0, method.getFailureCount());
    }

    @Test
    public void recordsFailedMethod() {
        ImprovementMethodStore store =
                new ImprovementMethodStore();

        ImprovementMethod method =
                new ImprovementMethod(
                        "method-b",
                        "Test method B",
                        "test",
                        "none",
                        "LOW"
                );

        store.add(method);

        ImprovementLearning learning =
                new ImprovementLearning(store);

        ImprovementLearningResult result =
                learning.record(method, false);

        assertTrue(result.isRecorded());
        assertFalse(result.isSuccess());
        assertEquals("method-b", result.getMethodName());
        assertEquals(-1, result.getScore());
        assertEquals(0, method.getSuccessCount());
        assertEquals(1, method.getFailureCount());
    }

    @Test
    public void bestMethodReflectsLearning() {
        ImprovementMethodStore store =
                new ImprovementMethodStore();

        ImprovementMethod weak =
                new ImprovementMethod(
                        "weak",
                        "Weak method",
                        "test",
                        "none",
                        "LOW"
                );

        ImprovementMethod strong =
                new ImprovementMethod(
                        "strong",
                        "Strong method",
                        "test",
                        "none",
                        "LOW"
                );

        store.add(weak);
        store.add(strong);

        ImprovementLearning learning =
                new ImprovementLearning(store);

        learning.record(weak, false);
        learning.record(strong, true);

        assertEquals(
                "strong",
                learning.findBestMethod().getName()
        );
    }

    @Test
    public void rejectsMissingMethod() {
        ImprovementMethodStore store =
                new ImprovementMethodStore();

        ImprovementLearning learning =
                new ImprovementLearning(store);

        ImprovementLearningResult result =
                learning.record(null, true);

        assertFalse(result.isRecorded());
        assertEquals(
                "Improvement method is missing",
                result.getMessage()
        );
    }
}
