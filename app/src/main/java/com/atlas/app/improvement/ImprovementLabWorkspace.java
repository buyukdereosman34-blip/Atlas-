package com.atlas.app.improvement;

public class ImprovementLabWorkspace {

    private final String id;
    private final String sourcePath;
    private final String workspacePath;
    private final String snapshotId;
    private final String snapshotPath;

    private ImprovementLabStatus status =
            ImprovementLabStatus.CREATED;

    public ImprovementLabWorkspace(
            String id,
            String sourcePath,
            String workspacePath,
            String snapshotId
    ) {
        this(
                id,
                sourcePath,
                workspacePath,
                snapshotId,
                null
        );
    }

    public ImprovementLabWorkspace(
            String id,
            String sourcePath,
            String workspacePath,
            String snapshotId,
            String snapshotPath
    ) {
        this.id = id;
        this.sourcePath = sourcePath;
        this.workspacePath = workspacePath;
        this.snapshotId = snapshotId;
        this.snapshotPath = snapshotPath;
    }

    public String getId() {
        return id;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public String getSnapshotId() {
        return snapshotId;
    }

    public String getSnapshotPath() {
        return snapshotPath;
    }

    public ImprovementLabStatus getStatus() {
        return status;
    }

    public void markReady() {
        if (status == ImprovementLabStatus.CREATED) {
            status = ImprovementLabStatus.READY;
        }
    }

    public void markChanged() {
        if (status == ImprovementLabStatus.READY) {
            status = ImprovementLabStatus.CHANGED;
        }
    }

    public void markValidated() {
        if (status == ImprovementLabStatus.CHANGED) {
            status = ImprovementLabStatus.VALIDATED;
        }
    }

    public void markPromoted() {
        if (status == ImprovementLabStatus.VALIDATED) {
            status = ImprovementLabStatus.PROMOTED;
        }
    }

    public void markFailed() {
        status = ImprovementLabStatus.FAILED;
    }

    public void markRolledBack() {
        status = ImprovementLabStatus.ROLLED_BACK;
    }
}
