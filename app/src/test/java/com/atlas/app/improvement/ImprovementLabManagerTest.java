package com.atlas.app.improvement;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ImprovementLabManagerTest {

    @Test
    public void createsReadyWorkspaceForValidProposal() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Improve Runtime",
                        "Fix runtime behavior",
                        Arrays.asList("AtlasRuntime.java"),
                        "Tests pass",
                        "LOW",
                        true,
                        new ImprovementValidation(
                                "Run tests",
                                "All tests pass"
                        )
                );

        ImprovementLabManager manager =
                new ImprovementLabManager();

        ImprovementLabWorkspace workspace =
                manager.createWorkspace(
                        proposal,
                        "/project/atlas-app",
                        "/project/atlas-lab"
                );

        assertNotNull(workspace);
        assertTrue(
                workspace.getId().startsWith("lab-")
        );
        assertTrue(
                workspace.getSnapshotId().startsWith("snapshot-")
        );
        assertEquals(
                ImprovementLabStatus.READY,
                workspace.getStatus()
        );
        assertEquals(
                "/project/atlas-app",
                workspace.getSourcePath()
        );
    }

    @Test
    public void rejectsMissingProposal() {
        ImprovementLabManager manager =
                new ImprovementLabManager();

        ImprovementLabWorkspace workspace =
                manager.createWorkspace(
                        null,
                        "/project/atlas-app",
                        "/project/atlas-lab"
                );

        assertEquals(null, workspace);
    }

    @Test
    public void rejectsMissingPaths() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Improve Runtime",
                        "Fix runtime behavior",
                        Arrays.asList("AtlasRuntime.java"),
                        "Tests pass",
                        "LOW",
                        true,
                        new ImprovementValidation(
                                "Run tests",
                                "All tests pass"
                        )
                );

        ImprovementLabManager manager =
                new ImprovementLabManager();

        assertEquals(
                null,
                manager.createWorkspace(
                        proposal,
                        "",
                        "/project/atlas-lab"
                )
        );

        assertEquals(
                null,
                manager.createWorkspace(
                        proposal,
                        "/project/atlas-app",
                        ""
                )
        );
    }

    @Test
    public void workspaceFollowsLifecycle() {
        ImprovementLabWorkspace workspace =
                new ImprovementLabWorkspace(
                        "lab-test",
                        "/project/atlas-app",
                        "/project/atlas-lab/lab-test",
                        "snapshot-test"
                );

        assertEquals(
                ImprovementLabStatus.CREATED,
                workspace.getStatus()
        );

        workspace.markReady();
        assertEquals(
                ImprovementLabStatus.READY,
                workspace.getStatus()
        );

        workspace.markChanged();
        assertEquals(
                ImprovementLabStatus.CHANGED,
                workspace.getStatus()
        );

        workspace.markValidated();
        assertEquals(
                ImprovementLabStatus.VALIDATED,
                workspace.getStatus()
        );

        workspace.markPromoted();
        assertEquals(
                ImprovementLabStatus.PROMOTED,
                workspace.getStatus()
        );
    }

    @Test
    public void failedWorkspaceCanBeRolledBack() {
        ImprovementLabWorkspace workspace =
                new ImprovementLabWorkspace(
                        "lab-test",
                        "/project/atlas-app",
                        "/project/atlas-lab/lab-test",
                        "snapshot-test"
                );

        workspace.markFailed();

        assertEquals(
                ImprovementLabStatus.FAILED,
                workspace.getStatus()
        );

        workspace.markRolledBack();

        assertEquals(
                ImprovementLabStatus.ROLLED_BACK,
                workspace.getStatus()
        );
    }
}
