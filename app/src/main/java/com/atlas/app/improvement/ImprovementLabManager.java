package com.atlas.app.improvement;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

public class ImprovementLabManager {

    public ImprovementLabWorkspace createWorkspace(
            ImprovementChangeProposal proposal,
            String sourcePath,
            String labRoot
    ) {
        if (proposal == null) return null;

        if (sourcePath == null || sourcePath.trim().isEmpty()) {
            return null;
        }

        if (labRoot == null || labRoot.trim().isEmpty()) {
            return null;
        }

        Path source = Paths.get(sourcePath.trim());
        Path root = Paths.get(labRoot.trim());

        if (!Files.exists(source)) {
            return null;
        }

        if (!Files.isDirectory(source)) {
            return null;
        }

        String id = "lab-" + UUID.randomUUID();
        String snapshotId = "snapshot-" + id;

        Path workspace = root.resolve(id);
        Path snapshotRoot =
                root.resolve("snapshots").resolve(snapshotId);

        try {
            Files.createDirectories(root);
            Files.createDirectories(workspace);
            Files.createDirectories(snapshotRoot);

            copyDirectoryContents(source, workspace);
            copyDirectoryContents(source, snapshotRoot);

            ImprovementLabWorkspace result =
                    new ImprovementLabWorkspace(
                            id,
                            source.toString(),
                            workspace.toString(),
                            snapshotId,
                            snapshotRoot.toString()
                    );

            result.markReady();

            return result;

        } catch (IOException e) {
            deleteIfExists(workspace);
            deleteIfExists(snapshotRoot);
            return null;
        }
    }

    public boolean archiveFailedWorkspace(
            ImprovementLabWorkspace workspace,
            String labRoot
    ) {
        if (workspace == null) return false;

        if (workspace.getStatus()
                != ImprovementLabStatus.FAILED) {
            return false;
        }

        if (labRoot == null || labRoot.trim().isEmpty()) {
            return false;
        }

        Path current =
                Paths.get(workspace.getWorkspacePath());

        Path archive =
                Paths.get(labRoot.trim())
                        .resolve("archive")
                        .resolve(workspace.getId());

        try {
            if (!Files.exists(current)) {
                return false;
            }

            Files.createDirectories(archive.getParent());

            Files.move(
                    current,
                    archive,
                    StandardCopyOption.ATOMIC_MOVE
            );

            return true;

        } catch (IOException atomicMoveFailure) {
            try {
                copyDirectory(current, archive);
                deleteIfExists(current);
                return true;
            } catch (IOException fallbackFailure) {
                return false;
            }
        }
    }

    private void copyDirectoryContents(
            Path source,
            Path target
    ) throws IOException {
        Files.walkFileTree(
                source,
                new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult preVisitDirectory(
                            Path dir,
                            BasicFileAttributes attrs
                    ) throws IOException {
                        Path relative =
                                source.relativize(dir);

                        Path destination =
                                target.resolve(relative);

                        Files.createDirectories(destination);

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(
                            Path file,
                            BasicFileAttributes attrs
                    ) throws IOException {
                        Path relative =
                                source.relativize(file);

                        Path destination =
                                target.resolve(relative);

                        Files.createDirectories(
                                destination.getParent()
                        );

                        Files.copy(
                                file,
                                destination,
                                StandardCopyOption.REPLACE_EXISTING
                        );

                        return FileVisitResult.CONTINUE;
                    }
                }
        );
    }

    private void copyDirectory(
            Path source,
            Path target
    ) throws IOException {
        Files.createDirectories(target);
        copyDirectoryContents(source, target);
    }

    private void deleteIfExists(Path path) {
        if (path == null || !Files.exists(path)) {
            return;
        }

        try {
            Files.walkFileTree(
                    path,
                    new SimpleFileVisitor<Path>() {

                        @Override
                        public FileVisitResult visitFile(
                                Path file,
                                BasicFileAttributes attrs
                        ) throws IOException {
                            Files.deleteIfExists(file);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(
                                Path dir,
                                IOException exception
                        ) throws IOException {
                            Files.deleteIfExists(dir);
                            return FileVisitResult.CONTINUE;
                        }
                    }
            );
        } catch (IOException ignored) {
            // Cleanup failure must not hide the original failure.
        }
    }
}
