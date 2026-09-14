package com.atlas.app.improvement;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public class ImprovementRealLabChangeApplier
        implements ImprovementLabChangeApplier {

    @Override
    public boolean apply(
            ImprovementChangeProposal proposal,
            ImprovementLabWorkspace workspace
    ) {
        if (proposal == null || workspace == null) {
            return false;
        }

        Map<String, String> changes =
                proposal.getProposedFileContents();

        if (changes == null || changes.isEmpty()) {
            return false;
        }

        try {
            File root =
                    new File(
                            workspace.getWorkspacePath()
                    );

            for (Map.Entry<String, String> entry
                    : changes.entrySet()) {

                String relativePath = entry.getKey();
                String content = entry.getValue();

                if (relativePath == null
                        || relativePath.trim().isEmpty()
                        || content == null) {
                    return false;
                }

                File target =
                        new File(
                                root,
                                relativePath
                        );

                String rootPath =
                        root.getCanonicalPath();

                String targetPath =
                        target.getCanonicalPath();

                if (!targetPath.equals(rootPath)
                        && !targetPath.startsWith(
                                rootPath + File.separator
                        )) {
                    return false;
                }

                File parent =
                        target.getParentFile();

                if (parent != null
                        && !parent.exists()
                        && !parent.mkdirs()) {
                    return false;
                }

                Files.write(
                        target.toPath(),
                        content.getBytes(
                                StandardCharsets.UTF_8
                        )
                );
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
