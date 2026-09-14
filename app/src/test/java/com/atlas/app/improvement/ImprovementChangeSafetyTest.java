package com.atlas.app.improvement;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImprovementChangeSafetyTest {

    @Test
    public void allowsCompleteLowRiskReversibleChange() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Improve Runtime",
                        "Fix a runtime problem",
                        Arrays.asList("AtlasRuntime.java"),
                        "Tests pass",
                        "LOW",
                        true,
                        new ImprovementValidation(
                                "Run tests",
                                "All tests pass"
                        )
                );

        ImprovementChangeSafetyReport report =
                new ImprovementChangeSafety()
                        .evaluate(proposal);

        assertEquals(
                ImprovementChangeSafetyDecision.ALLOW,
                report.getDecision()
        );
        assertTrue(report.isAllowed());
        assertTrue(report.isReversible());
    }

    @Test
    public void requiresReviewForHighRiskChange() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Change Android backend",
                        "Modify backend behavior",
                        Arrays.asList("AndroidBackend.java"),
                        "Backend tests pass",
                        "HIGH",
                        true,
                        new ImprovementValidation(
                                "Run backend tests",
                                "All tests pass"
                        )
                );

        ImprovementChangeSafetyReport report =
                new ImprovementChangeSafety()
                        .evaluate(proposal);

        assertEquals(
                ImprovementChangeSafetyDecision.REQUIRE_REVIEW,
                report.getDecision()
        );
    }

    @Test
    public void requiresReviewWhenChangeIsNotReversible() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Delete obsolete component",
                        "Remove unused component",
                        Arrays.asList("OldComponent.java"),
                        "Build passes",
                        "MEDIUM",
                        false,
                        new ImprovementValidation(
                                "Build project",
                                "Build succeeds"
                        )
                );

        ImprovementChangeSafetyReport report =
                new ImprovementChangeSafety()
                        .evaluate(proposal);

        assertEquals(
                ImprovementChangeSafetyDecision.REQUIRE_REVIEW,
                report.getDecision()
        );
    }

    @Test
    public void requiresReviewWhenAffectedFilesAreMissing() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Improve Runtime",
                        "Fix runtime behavior",
                        Collections.emptyList(),
                        "Tests pass",
                        "LOW",
                        true,
                        new ImprovementValidation(
                                "Run tests",
                                "All tests pass"
                        )
                );

        ImprovementChangeSafetyReport report =
                new ImprovementChangeSafety()
                        .evaluate(proposal);

        assertEquals(
                ImprovementChangeSafetyDecision.REQUIRE_REVIEW,
                report.getDecision()
        );
    }

    @Test
    public void deniesIncompleteProposal() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "",
                        "Why",
                        Arrays.asList("Test.java"),
                        "Pass",
                        "LOW",
                        true,
                        new ImprovementValidation(
                                "Test",
                                "Pass"
                        )
                );

        ImprovementChangeSafetyReport report =
                new ImprovementChangeSafety()
                        .evaluate(proposal);

        assertEquals(
                ImprovementChangeSafetyDecision.DENY,
                report.getDecision()
        );
        assertFalse(report.isAllowed());
    }

    @Test
    public void requiresReviewWhenValidationIsMissing() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Improve Runtime",
                        "Fix runtime behavior",
                        Arrays.asList("Runtime.java"),
                        "Tests pass",
                        "LOW",
                        true,
                        null
                );

        ImprovementChangeSafetyReport report =
                new ImprovementChangeSafety()
                        .evaluate(proposal);

        assertEquals(
                ImprovementChangeSafetyDecision.REQUIRE_REVIEW,
                report.getDecision()
        );
    }
}
