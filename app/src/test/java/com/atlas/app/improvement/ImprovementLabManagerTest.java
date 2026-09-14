package com.atlas.app.improvement;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ImprovementLabManagerTest {

    private ImprovementChangeProposal proposal() {
        return new ImprovementChangeProposal(
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
    }

    @Test
    public void createsRealWorkspaceAndSnapshot() throws Exception {
        Path root = Files.createTempDirectory("atlas-lab-test");
        Path source = root.resolve("source");
        Path labRoot = root.resolve("lab");

        Files.createDirectories(source);

        Path sourceFile =
                source.resolve("example.txt");

        Files.write(
                sourceFile,
                "original".getBytes(StandardCharsets.UTF_8)
        );

        ImprovementLabManager manager =
                new ImprovementLabManager();

        ImprovementLabWorkspace workspace =
                manager.createWorkspace(
                        proposal(),
                        source.toString(),
                        labRoot.toString()
                );

        assertNotNull(workspace);
        assertEquals(
                ImprovementLabStatus.READY,
                workspace.getStatus()
        );

        Path labFile =
                Path.of(workspace.getWorkspacePath())
                        .resolve("example.txt");

        Path snapshotFile =
                Path.of(workspace.getSnapshotPath())
                        .resolve("example.txt");

        assertTrue(Files.exists(labFile));
        assertTrue(Files.exists(snapshotFile));

        assertEquals(
                "original",
                new String(
                        Files.readAllBytes(labFile),
                        StandardCharsets.UTF_8
                )
        );

        assertEquals(
                "original",
                new String(
                        Files.readAllBytes(snapshotFile),
                        StandardCharsets.UTF_8
                )
        );
    }

    @Test
    public void changingLabDoesNotChangeSourceOrSnapshot()
            throws Exception {

        Path root = Files.createTempDirectory("atlas-lab-test");
        Path source = root.resolve("source");
        Path labRoot = root.resolve("lab");

        Files.createDirectories(source);

        Files.write(
                source.resolve("example.txt"),
                "original".getBytes(StandardCharsets.UTF_8)
        );

        ImprovementLabManager manager =
                new ImprovementLabManager();

        ImprovementLabWorkspace workspace =
                manager.createWorkspace(
                        proposal(),
                        source.toString(),
                        labRoot.toString()
                );

        Path labFile =
                Path.of(workspace.getWorkspacePath())
                        .resolve("example.txt");

        Files.write(
                labFile,
                "changed in lab"
                        .getBytes(StandardCharsets.UTF_8)
        );

        assertEquals(
                "original",
                new String(
                        Files.readAllBytes(source.resolve("example.txt")),
                        StandardCharsets.UTF_8
                )
        );

        assertEquals(
                "original",
                new String(
                        Files.readAllBytes(
                                Path.of(workspace.getSnapshotPath())
                                        .resolve("example.txt")
                        ),
                        StandardCharsets.UTF_8
                )
        );

        assertEquals(
                "changed in lab",
                new String(
                        Files.readAllBytes(labFile),
                        StandardCharsets.UTF_8
                )
        );
    }

    @Test
    public void archivesFailedWorkspaceWithoutDeletingHistory()
            throws Exception {

        Path root = Files.createTempDirectory("atlas-lab-test");
        Path source = root.resolve("source");
        Path labRoot = root.resolve("lab");

        Files.createDirectories(source);

        Files.write(
                source.resolve("example.txt"),
                "original".getBytes(StandardCharsets.UTF_8)
        );

        ImprovementLabManager manager =
                new ImprovementLabManager();

        ImprovementLabWorkspace workspace =
                manager.createWorkspace(
                        proposal(),
                        source.toString(),
                        labRoot.toString()
                );

        Path labFile =
                Path.of(workspace.getWorkspacePath())
                        .resolve("example.txt");

        Files.write(
                labFile,
                "failed experiment"
                        .getBytes(StandardCharsets.UTF_8)
        );

        workspace.markChanged();
        workspace.markFailed();

        boolean archived =
                manager.archiveFailedWorkspace(
                        workspace,
                        labRoot.toString()
                );

        assertTrue(archived);

        Path archiveFile =
                labRoot
                        .resolve("archive")
                        .resolve(workspace.getId())
                        .resolve("example.txt");

        assertTrue(Files.exists(archiveFile));
        assertFalse(
                Files.exists(
                        Path.of(workspace.getWorkspacePath())
                )
        );

        assertEquals(
                "failed experiment",
                new String(
                        Files.readAllBytes(archiveFile),
                        StandardCharsets.UTF_8
                )
        );

        assertTrue(
                Files.exists(
                        Path.of(workspace.getSnapshotPath())
                )
        );
    }

    @Test
    public void rejectsMissingProposal() throws Exception {
        Path root = Files.createTempDirectory("atlas-lab-test");

        ImprovementLabManager manager =
                new ImprovementLabManager();

        ImprovementLabWorkspace workspace =
                manager.createWorkspace(
                        null,
                        root.toString(),
                        root.resolve("lab").toString()
                );

        assertEquals(null, workspace);
    }

    @Test
    public void rejectsMissingPaths() {
        ImprovementLabManager manager =
                new ImprovementLabManager();

        assertEquals(
                null,
                manager.createWorkspace(
                        proposal(),
                        "",
                        "/project/atlas-lab"
                )
        );

        assertEquals(
                null,
                manager.createWorkspace(
                        proposal(),
                        "/project/atlas-app",
                        ""
                )
        );
    }

    @Test
    public void rejectsNonExistingSource() throws Exception {
        Path root = Files.createTempDirectory("atlas-lab-test");

        ImprovementLabManager manager =
                new ImprovementLabManager();

        assertEquals(
                null,
                manager.createWorkspace(
                        proposal(),
                        root.resolve("missing").toString(),
                        root.resolve("lab").toString()
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
