package com.atlas.app.improvement;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

public class ImprovementLabPromoter {

    public ImprovementLabPromotionResult promote(
            ImprovementLabWorkspace workspace,
            String targetPath
    ) {
        if (workspace == null) {
            return failure(
                    "LAB workspace is missing",
                    "",
                    targetPath
            );
        }

        if (workspace.getStatus()
                != ImprovementLabStatus.VALIDATED) {

            return failure(
                    "LAB workspace is not validated",
                    workspace.getWorkspacePath(),
                    targetPath
            );
        }

        if (targetPath == null
                || targetPath.trim().isEmpty()) {

            return failure(
                    "Promotion target is missing",
                    workspace.getWorkspacePath(),
                    targetPath
            );
        }

        Path source =
                Paths.get(workspace.getWorkspacePath())
                        .toAbsolutePath()
                        .normalize();

        Path target =
                Paths.get(targetPath)
                        .toAbsolutePath()
                        .normalize();

        if (!Files.isDirectory(source)) {
            return failure(
                    "LAB workspace does not exist",
                    source.toString(),
                    target.toString()
            );
        }

        if (!Files.isDirectory(target)) {
            return failure(
                    "Promotion target does not exist",
                    source.toString(),
                    target.toString()
            );
        }

        try {
            copyDirectory(source, target);

            workspace.markPromoted();

            return new ImprovementLabPromotionResult(
                    true,
                    "LAB workspace promoted successfully",
                    source.toString(),
                    target.toString()
            );

        } catch (IOException e) {
            return failure(
                    "Promotion failed: " + e,
                    source.toString(),
                    target.toString()
            );
        }
    }

    private ImprovementLabPromotionResult failure(
            String reason,
            String sourcePath,
            String targetPath
    ) {
        return new ImprovementLabPromotionResult(
                false,
                reason,
                sourcePath,
                targetPath
        );
    }

    private void copyDirectory(
            Path source,
            Path target
    ) throws IOException {

        Files.walkFileTree(
                source,
                new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult preVisitDirectory(
                            Path directory,
                            BasicFileAttributes attributes
                    ) throws IOException {

                        Path relative =
                                source.relativize(directory);

                        Path destination =
                                target.resolve(relative);

                        Files.createDirectories(destination);

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(
                            Path file,
                            BasicFileAttributes attributes
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
                                StandardCopyOption.REPLACE_EXISTING,
                                StandardCopyOption.COPY_ATTRIBUTES
                        );

                        return FileVisitResult.CONTINUE;
                    }
                }
        );
    }
}
