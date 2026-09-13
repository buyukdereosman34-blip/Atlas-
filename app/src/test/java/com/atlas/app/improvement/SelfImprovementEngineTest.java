package com.atlas.app.improvement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SelfImprovementEngineTest {

    @Test
    public void analyzeObservationCreatesProposal() {
        SelfImprovementEngine engine = new SelfImprovementEngine();

        ImprovementObservation observation =
                new ImprovementObservation(
                        "runtime",
                        "Control action requires permission",
                        "MEDIUM"
                );

        ImprovementProposal proposal =
                engine.analyze(observation);

        assertNotNull(proposal);
        assertEquals(
                "Investigate: Control action requires permission",
                proposal.getTitle()
        );
        assertEquals("runtime", observation.getSource());
        assertEquals("MEDIUM", observation.getSeverity());
    }

    @Test
    public void analyzeNullObservationReturnsNull() {
        SelfImprovementEngine engine = new SelfImprovementEngine();

        assertNull(engine.analyze(null));
    }

    @Test
    public void observeStoresObservation() {
        SelfImprovementEngine engine = new SelfImprovementEngine();

        ImprovementObservation observation =
                new ImprovementObservation(
                        "runtime",
                        "Permission required",
                        "MEDIUM"
                );

        engine.observe(observation);

        assertEquals(1, engine.getObservations().size());
        assertEquals(
                "Permission required",
                engine.getObservations().get(0).getMessage()
        );
    }

    @Test
    public void observeIgnoresNullObservation() {
        SelfImprovementEngine engine = new SelfImprovementEngine();

        engine.observe(null);

        assertEquals(0, engine.getObservations().size());
    }
}
