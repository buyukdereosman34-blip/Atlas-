package com.atlas.app.command;

import com.atlas.app.logging.AtlasLogger;
import com.atlas.app.runtime.AtlasRuntime;

public class CommandDispatcher {

    public interface Output {
        void print(String message);
    }

    private final AtlasLogger logger;
    private final Output output;
    private final ShellCommandExecutor shell;
    private final AtlasRuntime runtime;

    public CommandDispatcher(
            AtlasLogger logger,
            Output output,
            ShellCommandExecutor shell
    ) {
        this(
                logger,
                output,
                shell,
                null
        );
    }

    public CommandDispatcher(
            AtlasLogger logger,
            Output output,
            ShellCommandExecutor shell,
            AtlasRuntime runtime
    ) {
        this.logger = logger;
        this.output = output;
        this.shell = shell;
        this.runtime = runtime;
    }

    public void execute(String command) {

        if (command == null) {
            return;
        }

        command = command.trim();

        if (command.isEmpty()) {
            return;
        }

        logger.info("Command received: " + command);

        switch (command.toLowerCase()) {

            case "help":
                help();
                break;

            case "version":
                version();
                break;

            case "status":
                status();
                break;

            case "logs":
                logs();
                break;

            case "clear":
                output.print("__CLEAR__");
                break;

            default:
                if (command.toLowerCase().startsWith("tool ")) {
                    executeToolCommand(command);
                } else {
                    executeShell(command);
                }
                break;
        }
    }

    private void executeShell(String command) {

        logger.info("Shell command: " + command);

        ShellCommandExecutor.Result result =
                shell.execute(command);

        if (!result.output.isEmpty()) {
            output.print(result.output.trim());
        }

        if (result.exitCode != 0) {
            output.print(
                    "Process exited with code: "
                            + result.exitCode
            );

            logger.warn(
                    "Shell command failed: "
                            + result.exitCode
            );
        }
    }

    private void executeToolCommand(String command) {
        if (runtime == null) {
            output.print("Tool runtime is not connected");
            return;
        }

        String[] parts = command.trim().split("\\s+");

        if (parts.length < 2) {
            output.print("Usage: tool <name> <action> [args...]");
            return;
        }

        String toolName = parts[1];

        if ("list".equalsIgnoreCase(toolName)) {
            listTools();
            return;
        }

        if ("info".equalsIgnoreCase(toolName)) {
            if (parts.length < 3) {
                output.print("Usage: tool info <name>");
                return;
            }

            showToolInfo(parts[2]);
            return;
        }

        if (parts.length < 3) {
            output.print("Usage: tool <name> <action> [args...]");
            return;
        }

        String action = parts[2];

        String[] args = new String[Math.max(0, parts.length - 3)];

        if (parts.length > 3) {
            System.arraycopy(
                    parts,
                    3,
                    args,
                    0,
                    parts.length - 3
            );
        }

        logger.info(
                "Tool command: " + toolName + " " + action
        );

        com.atlas.app.tool.ToolResult result =
                runtime.executeTool(
                        toolName,
                        action,
                        args
                );

        if (!result.output.isEmpty()) {
            output.print(result.output.trim());
        }

        if (!result.success) {
            logger.warn(
                    "Tool failed: " + toolName +
                    " / " + action +
                    " / code=" + result.code
            );
        }
    }

    private void listTools() {
        output.print("ATLAS Tools:");

        for (com.atlas.app.tool.AtlasTool tool :
                runtime.getToolRegistry().getAll()) {

            output.print(
                    "  " + tool.getName() +
                    " - " + tool.getDescription()
            );
        }
    }

    private void showToolInfo(String toolName) {
        com.atlas.app.tool.AtlasTool tool =
                runtime.getTool(toolName);

        if (tool == null) {
            output.print("Tool not found: " + toolName);
            return;
        }

        output.print("Tool: " + tool.getName());
        output.print("Description: " + tool.getDescription());

        if ("screen".equalsIgnoreCase(tool.getName())) {
            output.print("Actions:");
            output.print("  status");
            output.print("  info");
        } else if ("apps".equalsIgnoreCase(tool.getName())) {
            output.print("Actions:");
            output.print("  status");
            output.print("  list");
        } else if ("control".equalsIgnoreCase(tool.getName())) {
            output.print("Actions:");
            output.print("  status");
        }
    }

    private void help() {

        output.print("ATLAS CLI commands:");
        output.print("  help     - show available commands");
        output.print("  version  - show ATLAS version");
        output.print("  status   - show runtime status");
        output.print("  logs     - show logging subsystem status");
        output.print("  clear    - clear terminal output");
        output.print("");
        output.print("Shell commands:");
        output.print("  Any Android/Unix shell command");
    }

    private void version() {

        output.print("ATLAS CLI version 0.1");
        output.print("Android interface: active");
    }

    private void status() {

        output.print("ATLAS status:");
        output.print("  UI:     active");
        output.print("  Logger: active");
        output.print("  CLI:    active");
        output.print("  Shell:  active");
        output.print("  PRoot:  suspended");
        output.print("  RootFS: suspended");
    }

    private void logs() {

        output.print("Logging subsystem: active");
        output.print(
                "Buffered entries: "
                        + logger.getBuffer().size()
        );

        for (String line :
                logger.getBuffer().snapshot()) {

            output.print(line);
        }
    }
}
