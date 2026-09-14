package com.atlas.app.improvement;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SelfImprovementChangeSafetyIntegrationTest {

    @Test
    public void engineEvaluatesSafeChangeProposal() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Improve Runtime",
                        "Fix runtime behavior",
                        Arrays.asList(
                                "AtlasRuntime.java"
                        ),
                        "All tests pass",
                        "LOW",
                        true,
                        new ImprovementValidation(
                                "Run tests",
                                "All tests pass"
                        )
                );

        ImprovementChangeSafetyReport report =
                engine.evaluateChangeSafety(proposal);

        assertEquals(
                ImprovementChangeSafetyDecision.ALLOW,
                report.getDecision()
        );

        assertTrue(report.isAllowed());
    }

    @Test
    public void engineRequiresReviewForHighRiskChange() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Modify Android backend",
                        "Change backend behavior",
                        Arrays.asList(
                                "AndroidBackend.java"
                        ),
                        "Backend tests pass",
                        "HIGH",
                        true,
                        new ImprovementValidation(
                                "Run backend tests",
                                "All tests pass"
                        )
                );

        ImprovementChangeSafetyReport report =
                engine.evaluateChangeSafety(proposal);

        assertEquals(
                ImprovementChangeSafetyDecision.REQUIRE_REVIEW,
                report.getDecision()
        );
    }
}
