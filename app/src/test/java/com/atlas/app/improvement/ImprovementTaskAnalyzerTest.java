package com.atlas.app.improvement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ImprovementTaskAnalyzerTest {

    @Test
    public void analyzesTaskUsingBestMethod() {
        ImprovementMethodStore store =
                new ImprovementMethodStore();

        ImprovementMethod method =
                new ImprovementMethod(
                        "Runtime inspection",
                        "Inspect runtime",
                        "ATLAS",
                        "source access",
                        "LOW"
                );

        method.recordSuccess();
        store.add(method);

        ImprovementTaskAnalyzer analyzer =
                new ImprovementTaskAnalyzer(store);

        ImprovementTask task =
                new ImprovementTask(
                        "Improve Runtime",
                        "Find an improvement in AtlasRuntime"
                );

        ImprovementFinding finding =
                analyzer.analyze(task);

        assertNotNull(finding);
        assertEquals("ATLAS", finding.getArea());
        assertEquals(
                "Find an improvement in AtlasRuntime",
                finding.getProblem()
        );
        assertEquals(
                "Use method: Runtime inspection",
                finding.getRecommendation()
        );
    }
}
