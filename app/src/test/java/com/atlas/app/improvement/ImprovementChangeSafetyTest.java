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

    @Test
    public void requiresReviewForAndroidManifest() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Update manifest",
                        "Add new permission",
                        Arrays.asList("app/src/main/AndroidManifest.xml"),
                        "Build passes",
                        "LOW",
                        true,
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
    public void requiresReviewForGradleFile() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Update gradle",
                        "Change build config",
                        Arrays.asList("app/build.gradle"),
                        "Build passes",
                        "LOW",
                        true,
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
    public void requiresReviewForImprovementPath() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Self-improve",
                        "Modify improvement loop",
                        Arrays.asList(
                                "app/src/main/java/com/atlas/app/improvement/"
                                        + "ImprovementSelfDevelopmentLoop.java"
                        ),
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
    public void requiresReviewForPolicyPath() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Update policy",
                        "Change policy engine",
                        Arrays.asList(
                                "app/src/main/java/com/atlas/app/policy/"
                                        + "PolicyEngine.java"
                        ),
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
    public void requiresReviewForMoreThanFiveFiles() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Large refactor",
                        "Touch many files",
                        Arrays.asList(
                                "src/A.java",
                                "src/B.java",
                                "src/C.java",
                                "src/D.java",
                                "src/E.java",
                                "src/F.java"
                        ),
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
    public void allowsExactlyFiveFiles() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Small refactor",
                        "Touch exactly five files",
                        Arrays.asList(
                                "src/A.java",
                                "src/B.java",
                                "src/C.java",
                                "src/D.java",
                                "src/E.java"
                        ),
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
    }
}
