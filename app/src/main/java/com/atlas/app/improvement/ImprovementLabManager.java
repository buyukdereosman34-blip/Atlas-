package com.atlas.app.improvement;

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

        String id =
                "lab-" + UUID.randomUUID();

        String workspacePath =
                labRoot.trim()
                        + "/" + id;

        String snapshotId =
                "snapshot-" + id;

        ImprovementLabWorkspace workspace =
                new ImprovementLabWorkspace(
                        id,
                        sourcePath.trim(),
                        workspacePath,
                        snapshotId
                );

        workspace.markReady();

        return workspace;
    }
}
