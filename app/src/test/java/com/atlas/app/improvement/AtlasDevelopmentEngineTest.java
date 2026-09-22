package com.atlas.app.improvement;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;

public class AtlasDevelopmentEngineTest {

    @Test
    public void developCreatesResult() {
        AtlasDevelopmentEngine engine =
                new AtlasDevelopmentEngine();

        ImprovementSelfDevelopmentResult result =
                engine.develop(
                        "Test improvement",
                        "replace TEST_OLD with TEST_NEW",
                        Collections.emptyList(),
                        ".",
                        "build/atlas-lab",
                        "build/atlas-target"
                );

        assertNotNull(result);
    }
}
