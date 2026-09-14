package com.atlas.app.improvement;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNotNull;

public class SelfImprovementEngineTest {

    @Test
    public void analyzeObservationCreatesProposal() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        ImprovementObservation observation =
                new ImprovementObservation(
                        "runtime",
                        "Tool execution failed",
                        "MEDIUM"
                );

        ImprovementProposal proposal =
                engine.analyze(observation);

        assertNotNull(proposal);
        assertEquals(
                "Investigate: Tool execution failed",
                proposal.getTitle()
        );
    }

    @Test
    public void analyzeNullObservationReturnsNull() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        assertEquals(
                null,
                engine.analyze(null)
        );
    }

    @Test
    public void observeStoresObservation() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        ImprovementObservation observation =
                new ImprovementObservation(
                        "runtime",
                        "Failure",
                        "MEDIUM"
                );

        engine.observe(observation);

        List<ImprovementObservation> observations =
                engine.getObservations();

        assertEquals(1, observations.size());
        assertSame(observation, observations.get(0));
    }

    @Test
    public void observeIgnoresNullObservation() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        engine.observe(null);

        assertEquals(
                0,
                engine.getObservations().size()
        );
    }

    @Test
    public void analyzesTaskThroughMethodStore() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        ImprovementMethod method =
                new ImprovementMethod(
                        "Runtime inspection",
                        "Inspect runtime",
                        "ATLAS",
                        "source access",
                        "LOW"
                );

        engine.addMethod(method);

        ImprovementFinding finding =
                engine.analyzeTask(
                        new ImprovementTask(
                                "Improve Runtime",
                                "Find a runtime improvement"
                        )
                );

        assertNotNull(finding);
        assertEquals(
                "Use method: Runtime inspection",
                finding.getRecommendation()
        );
    }

    @Test
    public void executesTaskWithMethodFallback() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        ImprovementMethod first =
                new ImprovementMethod(
                        "First",
                        "First method",
                        "test",
                        "none",
                        "LOW"
                );

        ImprovementMethod second =
                new ImprovementMethod(
                        "Second",
                        "Second method",
                        "test",
                        "none",
                        "LOW"
                );

        first.recordSuccess();

        engine.addMethod(first);
        engine.addMethod(second);

        ImprovementMethod result =
                engine.executeTask(
                        new ImprovementTask(
                                "Test fallback",
                                "Test method fallback"
                        ),
                        method -> method == second
                );

        assertSame(second, result);
        assertEquals(1, first.getFailureCount());
        assertEquals(1, second.getSuccessCount());
    }

    @Test
    public void taskExecutionWithoutMethodsReturnsNull() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        ImprovementMethod result =
                engine.executeTask(
                        new ImprovementTask(
                                "Test",
                                "No methods"
                        ),
                        method -> true
                );

        assertEquals(null, result);
    }
}
