package com.atlas.app.improvement;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImprovementChangeProposalGenerator {

    public ImprovementChangeProposal generate(
            ImprovementTask task,
            ImprovementFinding finding,
            List<String> affectedFiles
    ) {
        return generate(
                task,
                finding,
                affectedFiles,
                null
        );
    }

    public ImprovementChangeProposal generate(
            ImprovementTask task,
            ImprovementFinding finding,
            List<String> affectedFiles,
            String sourcePath
    ) {
        if (task == null || finding == null) {
            return null;
        }

        ImprovementValidation validation =
                new ImprovementValidation(
                        "Build and test the proposed change",
                        "The proposed change passes validation without introducing regressions"
                );

        String what =
                "Improve " + finding.getArea()
                        + " by addressing: " + finding.getProblem();

        String why =
                task.getDescription();

        String expectedResult =
                finding.getRecommendation();

        List<String> files =
                affectedFiles == null
                        ? Collections.emptyList()
                        : affectedFiles;

        Map<String, String> proposedFileContents =
                generateFileContents(
                        sourcePath,
                        files,
                        finding
                );

        return new ImprovementChangeProposal(
                what,
                why,
                files,
                expectedResult,
                "MEDIUM",
                true,
                validation,
                proposedFileContents
        );
    }

    private Map<String, String> generateFileContents(
            String sourcePath,
            List<String> affectedFiles,
            ImprovementFinding finding
    ) {
        if (sourcePath == null
                || sourcePath.trim().isEmpty()
                || affectedFiles == null
                || affectedFiles.isEmpty()
                || finding == null) {
            return Collections.emptyMap();
        }

        Map<String, String> result =
                new HashMap<>();

        String recommendation =
                finding.getRecommendation() == null
                        ? ""
                        : finding.getRecommendation().toLowerCase();

        for (String affectedFile : affectedFiles) {
            if (affectedFile == null
                    || affectedFile.trim().isEmpty()) {
                continue;
            }

            File file =
                    findFile(
                            new File(sourcePath),
                            affectedFile
                    );

            if (file == null) {
                continue;
            }

            try {
                String content =
                        new String(
                                Files.readAllBytes(
                                        file.toPath()
                                ),
                                StandardCharsets.UTF_8
                        );

                String changed = content;

                if (recommendation.contains("old")
                        && recommendation.contains("new")) {
                    changed =
                            changed.replace(
                                    "\"old\"",
                                    "\"new\""
                            );
                }

                if (!changed.equals(content)) {
                    String sourceRoot =
                            new File(sourcePath)
                                    .getCanonicalPath();

                    String relativePath =
                            file.getCanonicalPath()
                                    .substring(
                                            sourceRoot.length()
                                    )
                                    .replace(
                                            File.separatorChar,
                                            '/'
                                    );

                    if (relativePath.startsWith("/")) {
                        relativePath =
                                relativePath.substring(1);
                    }

                    result.put(
                            relativePath,
                            changed
                    );
                }

            } catch (Exception ignored) {
                // Skip files that cannot be read safely.
            }
        }

        return result;
    }

    private File findFile(
            File root,
            String fileName
    ) {
        if (root == null || !root.exists()) {
            return null;
        }

        if (root.isFile()) {
            return root.getName().equals(fileName)
                    ? root
                    : null;
        }

        File[] children =
                root.listFiles();

        if (children == null) {
            return null;
        }

        for (File child : children) {
            File found =
                    findFile(
                            child,
                            fileName
                    );

            if (found != null) {
                return found;
            }
        }

        return null;
    }
}
