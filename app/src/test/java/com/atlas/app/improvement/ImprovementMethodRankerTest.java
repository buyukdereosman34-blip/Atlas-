package com.atlas.app.improvement;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ImprovementMethodRankerTest {

    @Test
    public void ranksMethodsByScore() {
        ImprovementMethod weak =
                new ImprovementMethod(
                        "Weak",
                        "Weak method",
                        "test",
                        "none",
                        "LOW"
                );

        ImprovementMethod medium =
                new ImprovementMethod(
                        "Medium",
                        "Medium method",
                        "test",
                        "none",
                        "LOW"
                );

        ImprovementMethod strong =
                new ImprovementMethod(
                        "Strong",
                        "Strong method",
                        "test",
                        "none",
                        "LOW"
                );

        weak.recordFailure();

        medium.recordSuccess();

        strong.recordSuccess();
        strong.recordSuccess();

        ImprovementMethodRanker ranker =
                new ImprovementMethodRanker();

        List<ImprovementMethod> ranked =
                ranker.rank(
                        Arrays.asList(
                                weak,
                                strong,
                                medium
                        )
                );

        assertEquals(3, ranked.size());
        assertSame(strong, ranked.get(0));
        assertSame(medium, ranked.get(1));
        assertSame(weak, ranked.get(2));
    }

    @Test
    public void returnsBestMethod() {
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

        second.recordSuccess();

        ImprovementMethodRanker ranker =
                new ImprovementMethodRanker();

        assertSame(
                second,
                ranker.best(
                        Arrays.asList(first, second)
                )
        );
    }

    @Test
    public void emptyMethodsReturnEmptyRanking() {
        ImprovementMethodRanker ranker =
                new ImprovementMethodRanker();

        assertEquals(
                0,
                ranker.rank(null).size()
        );

        assertEquals(
                null,
                ranker.best(null)
        );
    }
}
