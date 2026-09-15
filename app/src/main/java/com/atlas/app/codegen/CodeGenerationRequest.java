package com.atlas.app.codegen;

import java.util.Collections;
import java.util.List;

public class CodeGenerationRequest {

    private final String taskDescription;
    private final String sourcePath;
    private final List<String> affectedFiles;

    public CodeGenerationRequest(
            String taskDescription,
            String sourcePath,
            List<String> affectedFiles
    ) {
        this.taskDescription = taskDescription;
        this.sourcePath = sourcePath;
        this.affectedFiles =
                affectedFiles == null
                        ? Collections.emptyList()
                        : affectedFiles;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public List<String> getAffectedFiles() {
        return affectedFiles;
    }
}
