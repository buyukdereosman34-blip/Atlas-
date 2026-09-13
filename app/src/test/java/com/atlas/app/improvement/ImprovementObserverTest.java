package com.atlas.app.improvement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImprovementObserverTest {

    @Test
    public void observerReceivesObservation() {
        final ImprovementObservation[] received =
                new ImprovementObservation[1];

        ImprovementObserver observer =
                new ImprovementObserver() {
                    @Override
                    public void onObservation(
                            ImprovementObservation observation
                    ) {
                        received[0] = observation;
                    }
                };

        ImprovementObservation observation =
                new ImprovementObservation(
                        "runtime",
                        "Permission required",
                        "MEDIUM"
                );

        observer.onObservation(observation);

        assertEquals(observation, received[0]);
    }
}
