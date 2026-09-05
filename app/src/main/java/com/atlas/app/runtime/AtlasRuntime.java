package com.atlas.app.runtime;

import com.atlas.app.command.ShellCommandExecutor;
import com.atlas.app.tool.ToolResult;

public class AtlasRuntime {

    private final ShellCommandExecutor shell;

    public AtlasRuntime(String workingDirectory) {
        this.shell = new ShellCommandExecutor(workingDirectory);
    }

    public ToolResult executeShell(String command) {
        if (command == null || command.trim().isEmpty()) {
            return ToolResult.failure(
                    "Command is empty",
                    -1
            );
        }

        ShellCommandExecutor.Result result =
                shell.execute(command);

        if (result.exitCode == 0) {
            return ToolResult.success(
                    result.output.trim()
            );
        }

        return ToolResult.failure(
                result.output.trim(),
                result.exitCode
        );
    }

    public String getWorkingDirectory() {
        return shell.getWorkingDirectory();
    }
}
