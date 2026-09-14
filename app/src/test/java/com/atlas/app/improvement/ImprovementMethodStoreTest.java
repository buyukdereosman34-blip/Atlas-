package com.atlas.app.improvement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ImprovementMethodStoreTest {

    @Test
    public void storesAndFindsBestMethod() {
        ImprovementMethodStore store =
                new ImprovementMethodStore();

        ImprovementMethod weak =
                new ImprovementMethod(
                        "Weak",
                        "Weak method",
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

        strong.recordSuccess();
        strong.recordSuccess();

        store.add(weak);
        store.add(strong);

        assertEquals(2, store.size());
        assertSame(strong, store.findBest());
    }

    @Test
    public void emptyStoreReturnsNull() {
        ImprovementMethodStore store =
                new ImprovementMethodStore();

        assertEquals(null, store.findBest());
    }
}
