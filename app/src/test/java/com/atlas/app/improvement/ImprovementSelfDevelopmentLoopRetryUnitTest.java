package com.atlas.app.improvement;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Pure retry-loop unit test.
 *
 * This test does NOT call real gradle. It injects a fake build executor
 * that returns synthetic results. Its goal is to verify the retry
 * decision mechanism in isolation from environment/toolchain issues.
 */
public class ImprovementSelfDevelopmentLoopRetryUnitTest {

    @Test
    public void retriesWhenFirstAttemptHasCodeFailure() throws Exception {

        Path root = Files.createTempDirectory("atlas-retry-unit-");

        File source = new File(root.toFile(), "source");
        File labRoot = new File(root.toFile(), "lab");
        File target = new File(root.toFile(), "target");

        assertTrue(source.mkdirs());
        assertTrue(labRoot.mkdirs());
        assertTrue(target.mkdirs());

        ImprovementMethodStore store = new ImprovementMethodStore();
        store.add(new ImprovementMethod(
                "unit-retry",
                "Unit retry test method",
                "test",
                "none",
                "LOW"
        ));

        ImprovementTask task = new ImprovementTask(
                "Unit retry",
                "Verify retry logic without gradle"
        );

        ImprovementFinding finding = new ImprovementFinding(
                "Unit",
                "Simulated first-attempt code error",
                "Retry should recover"
        );

        final int[] fixCalls = {0};

        ImprovementFixer fixer = (
                t, f, failedProposal, buildResult, evalResult
        ) -> {
            fixCalls[0]++;
            return new ImprovementChangeProposal(
                    "Fix",
                    "Retry",
                    Collections.singletonList("Example.java"),
                    "Compiles",
                    "LOW",
                    true,
                    new ImprovementValidation(
                            "Compiles",
                            "Compiles"
                    ),
                    Collections.singletonMap(
                            "Example.java",
                            "public class Example {}"
                    )
            );
        };

        FakeBuildExecutor fakeExecutor = new FakeBuildExecutor(
                new ImprovementLabBuildResult(
                        false, false, 1, -1,
                        "error: cannot find symbol\n  symbol: class Foo",
                        ""
                ),
                new ImprovementLabBuildResult(
                        true, true, 0, 0,
                        "BUILD SUCCESSFUL",
                        "Tests passed"
                )
        );

        ImprovementSelfDevelopmentLoop loop =
                new ImprovementSelfDevelopmentLoop(
                        store,
                        null,
                        fixer,
                        fakeExecutor
                );

        ImprovementLabChangeApplier noopApplier =
                (proposal, workspace) -> true;

        ImprovementSelfDevelopmentResult result = loop.execute(
                task,
                finding,
                Collections.singletonList("Example.java"),
                source.getAbsolutePath(),
                labRoot.getAbsolutePath(),
                target.getAbsolutePath(),
                noopApplier
        );

        assertNotNull(result);
        assertTrue(
                "Expected success on second attempt. "
                        + "Stage=" + result.getStage()
                        + " Message=" + result.getMessage(),
                result.isSuccess()
        );
        assertEquals("COMPLETED", result.getStage());
        assertTrue(
                "Should complete on attempt 2, got: "
                        + result.getMessage(),
                result.getMessage().contains("attempt 2")
        );

        assertEquals(
                "Fixer must be called exactly once",
                1,
                fixCalls[0]
        );
        assertEquals(
                "Fake executor must be invoked twice (attempt 1 + 2)",
                2,
                fakeExecutor.callCount
        );
    }

    @Test
    public void stopsImmediatelyOnEnvironmentFailure() throws Exception {

        Path root = Files.createTempDirectory("atlas-retry-env-");

        File source = new File(root.toFile(), "source");
        File labRoot = new File(root.toFile(), "lab");
        File target = new File(root.toFile(), "target");

        assertTrue(source.mkdirs());
        assertTrue(labRoot.mkdirs());
        assertTrue(target.mkdirs());

        ImprovementMethodStore store = new ImprovementMethodStore();
        store.add(new ImprovementMethod(
                "env-stop",
                "Environment stop test",
                "test",
                "none",
                "LOW"
        ));

        ImprovementTask task = new ImprovementTask(
                "Env stop",
                "Environment failure should not retry"
        );

        ImprovementFinding finding = new ImprovementFinding(
                "Env",
                "AAPT2 daemon startup failed",
                "Should stop immediately"
        );

        final int[] fixCalls = {0};

        ImprovementFixer fixer = (
                t, f, p, b, e
        ) -> {
            fixCalls[0]++;
            return null;
        };

        FakeBuildExecutor fakeExecutor = new FakeBuildExecutor(
                new ImprovementLabBuildResult(
                        false, false, 1, -1,
                        "AAPT2 bin Daemon #0: Daemon startup failed",
                        ""
                ),
                new ImprovementLabBuildResult(
                        true, true, 0, 0,
                        "BUILD SUCCESSFUL",
                        ""
                )
        );

        ImprovementSelfDevelopmentLoop loop =
                new ImprovementSelfDevelopmentLoop(
                        store,
                        null,
                        fixer,
                        fakeExecutor
                );

        ImprovementLabChangeApplier noopApplier =
                (proposal, workspace) -> true;

        ImprovementSelfDevelopmentResult result = loop.execute(
                task,
                finding,
                Collections.singletonList("Example.java"),
                source.getAbsolutePath(),
                labRoot.getAbsolutePath(),
                target.getAbsolutePath(),
                noopApplier
        );

        assertNotNull(result);
        assertTrue(
                "Environment failure must not succeed",
                !result.isSuccess()
        );

        ImprovementFailureClassification cls =
                result.getFailureClassification();

        assertNotNull(cls);
        assertEquals(
                ImprovementFailureCategory.TOOLCHAIN,
                cls.getCategory()
        );
        assertTrue(cls.isEnvironmentIssue());

        assertEquals(
                "Fixer must NOT be called on environment failure",
                0,
                fixCalls[0]
        );
        assertEquals(
                "Fake executor must be invoked only once",
                1,
                fakeExecutor.callCount
        );
        assertTrue(
                "Should stop after 1 attempt, got: "
                        + result.getMessage(),
                result.getMessage().contains("1 attempt")
        );
    }

    private static class FakeBuildExecutor
            extends ImprovementLabBuildExecutor {

        private final ImprovementLabBuildResult[] responses;
        int callCount = 0;

        FakeBuildExecutor(ImprovementLabBuildResult... responses) {
            this.responses = responses;
        }

        @Override
        public ImprovementLabBuildResult buildAndTest(
                ImprovementLabWorkspace workspace
        ) {
            int idx = Math.min(callCount, responses.length - 1);
            ImprovementLabBuildResult r = responses[idx];
            callCount++;
            return r;
        }
    }
}
