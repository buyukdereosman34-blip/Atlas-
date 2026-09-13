package com.atlas.app.improvement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ImprovementObservationCollectorTest {

    @Test
    public void collectValidObservation() {
        ImprovementObservationCollector collector =
                new ImprovementObservationCollector();

        ImprovementObservation observation =
                collector.collect(
                        "runtime",
                        "Permission required",
                        "MEDIUM"
                );

        assertNotNull(observation);
        assertEquals("runtime", observation.getSource());
        assertEquals("Permission required", observation.getMessage());
        assertEquals("MEDIUM", observation.getSeverity());
    }

    @Test
    public void collectInvalidObservationReturnsNull() {
        ImprovementObservationCollector collector =
                new ImprovementObservationCollector();

        assertNull(
                collector.collect(
                        "",
                        "Permission required",
                        "MEDIUM"
                )
        );
    }
}
