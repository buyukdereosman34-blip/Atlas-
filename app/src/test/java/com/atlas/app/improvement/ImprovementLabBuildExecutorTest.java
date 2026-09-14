package com.atlas.app.improvement;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImprovementLabBuildExecutorTest {

    @Test
    public void rejectsMissingWorkspace() {
        ImprovementLabBuildExecutor executor =
                new ImprovementLabBuildExecutor();

        ImprovementLabBuildResult result =
                executor.buildAndTest(null);

        assertFalse(result.isSuccessful());
        assertFalse(result.isBuildSuccess());
    }

    @Test
    public void rejectsNonGradleWorkspace() throws Exception {
        Path root =
                Files.createTempDirectory("atlas-lab-build-test");

        ImprovementLabWorkspace workspace =
                new ImprovementLabWorkspace(
                        "lab-test",
                        root.toString(),
                        root.toString(),
                        "snapshot-test"
                );

        workspace.markReady();

        ImprovementLabBuildExecutor executor =
                new ImprovementLabBuildExecutor();

        ImprovementLabBuildResult result =
                executor.buildAndTest(workspace);

        assertFalse(result.isSuccessful());
        assertFalse(result.isBuildSuccess());
        assertTrue(
                result.getBuildOutput()
                        .contains("not a Gradle project")
        );
    }
}
