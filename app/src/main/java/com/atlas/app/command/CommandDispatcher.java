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
        this(logger, output, shell, null);
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

    private void executeToolCommand(String command) {
        if (runtime == null) {
            output.print("Runtime bagli degil");
            return;
        }
        String[] parts = command.trim().split("\\s+");
        if (parts.length < 3) {
            output.print("Usage: tool <name> <action> [args...]");
            return;
        }
        String toolName = parts[1];
        String action = parts[2];
        String[] args = new String[Math.max(0, parts.length - 3)];
        if (parts.length > 3) {
            System.arraycopy(parts, 3, args, 0, parts.length - 3);
        }
        com.atlas.app.tool.ToolResult r = runtime.executeTool(toolName, action, args);
        output.print(r.output);
        logger.info("Tool: " + toolName + "/" + action + " -> " + r.code);
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
