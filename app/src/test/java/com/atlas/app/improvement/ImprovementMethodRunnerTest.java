package com.atlas.app.improvement;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertSame;

public class ImprovementMethodRunnerTest {

    @Test
    public void fallsBackWhenBestMethodFails() {
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
        second.recordSuccess();

        ImprovementMethodRunner runner =
                new ImprovementMethodRunner(
                        new ImprovementMethodRanker()
                );

        ImprovementMethod result =
                runner.run(
                        Arrays.asList(first, second),
                        method -> method == second
                );

        assertSame(second, result);
    }

    @Test
    public void returnsNullWhenAllMethodsFail() {
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

        ImprovementMethodRunner runner =
                new ImprovementMethodRunner(
                        new ImprovementMethodRanker()
                );

        ImprovementMethod result =
                runner.run(
                        Arrays.asList(first, second),
                        method -> false
                );

        assertSame(null, result);
    }

    @Test
    public void recordsFailureAndSuccess() {
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

        ImprovementMethodRunner runner =
                new ImprovementMethodRunner(
                        new ImprovementMethodRanker()
                );

        runner.run(
                Arrays.asList(first, second),
                method -> method == second
        );

        assertSame(1, first.getFailureCount());
        assertSame(1, second.getSuccessCount());
    }
}
