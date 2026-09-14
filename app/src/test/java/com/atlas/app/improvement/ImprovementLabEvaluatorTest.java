package com.atlas.app.improvement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImprovementLabEvaluatorTest {

    @Test
    public void successfulBuildAndTestValidateWorkspace() {
        ImprovementLabWorkspace workspace =
                new ImprovementLabWorkspace(
                        "lab-success",
                        "/tmp/source",
                        "/tmp/workspace",
                        "snapshot-success"
                );

        workspace.markReady();

        ImprovementLabBuildResult buildResult =
                new ImprovementLabBuildResult(
                        true,
                        true,
                        0,
                        0,
                        "BUILD SUCCESSFUL",
                        "TESTS PASSED"
                );

        ImprovementLabEvaluator evaluator =
                new ImprovementLabEvaluator();

        ImprovementLabEvaluationResult result =
                evaluator.evaluate(
                        workspace,
                        buildResult
                );

        assertTrue(result.isPassed());
        assertEquals(
                ImprovementLabStatus.VALIDATED,
                result.getStatus()
        );
        assertEquals(
                ImprovementLabStatus.VALIDATED,
                workspace.getStatus()
        );
    }

    @Test
    public void failedBuildMarksWorkspaceFailed() {
        ImprovementLabWorkspace workspace =
                new ImprovementLabWorkspace(
                        "lab-build-fail",
                        "/tmp/source",
                        "/tmp/workspace",
                        "snapshot-build-fail"
                );

        workspace.markReady();

        ImprovementLabBuildResult buildResult =
                new ImprovementLabBuildResult(
                        false,
                        false,
                        1,
                        -1,
                        "BUILD FAILED",
                        ""
                );

        ImprovementLabEvaluator evaluator =
                new ImprovementLabEvaluator();

        ImprovementLabEvaluationResult result =
                evaluator.evaluate(
                        workspace,
                        buildResult
                );

        assertFalse(result.isPassed());
        assertEquals(
                ImprovementLabStatus.FAILED,
                result.getStatus()
        );
        assertEquals(
                ImprovementLabStatus.FAILED,
                workspace.getStatus()
        );
        assertEquals(
                "LAB build failed",
                result.getReason()
        );
    }

    @Test
    public void failedTestMarksWorkspaceFailed() {
        ImprovementLabWorkspace workspace =
                new ImprovementLabWorkspace(
                        "lab-test-fail",
                        "/tmp/source",
                        "/tmp/workspace",
                        "snapshot-test-fail"
                );

        workspace.markReady();

        ImprovementLabBuildResult buildResult =
                new ImprovementLabBuildResult(
                        true,
                        false,
                        0,
                        1,
                        "BUILD SUCCESSFUL",
                        "TEST FAILED"
                );

        ImprovementLabEvaluator evaluator =
                new ImprovementLabEvaluator();

        ImprovementLabEvaluationResult result =
                evaluator.evaluate(
                        workspace,
                        buildResult
                );

        assertFalse(result.isPassed());
        assertEquals(
                ImprovementLabStatus.FAILED,
                result.getStatus()
        );
        assertEquals(
                ImprovementLabStatus.FAILED,
                workspace.getStatus()
        );
        assertEquals(
                "LAB tests failed",
                result.getReason()
        );
    }

    @Test
    public void missingBuildResultFailsEvaluation() {
        ImprovementLabWorkspace workspace =
                new ImprovementLabWorkspace(
                        "lab-missing-result",
                        "/tmp/source",
                        "/tmp/workspace",
                        "snapshot-missing-result"
                );

        workspace.markReady();

        ImprovementLabEvaluator evaluator =
                new ImprovementLabEvaluator();

        ImprovementLabEvaluationResult result =
                evaluator.evaluate(
                        workspace,
                        null
                );

        assertFalse(result.isPassed());
        assertEquals(
                ImprovementLabStatus.FAILED,
                result.getStatus()
        );
        assertEquals(
                ImprovementLabStatus.FAILED,
                workspace.getStatus()
        );
    }
}
