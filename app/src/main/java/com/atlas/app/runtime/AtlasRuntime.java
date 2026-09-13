package com.atlas.app.runtime;

import android.content.Context;

import com.atlas.app.command.ShellCommandExecutor;
import com.atlas.app.policy.PolicyDecision;
import com.atlas.app.policy.PolicyEngine;
import com.atlas.app.policy.PolicyRequest;
import com.atlas.app.permission.PermissionResolver;
import com.atlas.app.permission.PermissionType;
import com.atlas.app.tool.AtlasTool;
import com.atlas.app.tool.ToolRegistry;
import com.atlas.app.tool.ToolResult;
import com.atlas.app.tool.apps.AppsTool;
import com.atlas.app.tool.screen.ScreenTool;
import com.atlas.app.tool.control.ControlTool;

public class AtlasRuntime {

    private final ShellCommandExecutor shell;
    private final ToolRegistry toolRegistry;
    private final PolicyEngine policyEngine;
    private final PermissionResolver permissionResolver;

    public AtlasRuntime(String workingDirectory) {
        this.shell = new ShellCommandExecutor(workingDirectory);

        this.toolRegistry = new ToolRegistry();
        this.policyEngine = new PolicyEngine();
        this.permissionResolver = new PermissionResolver();
        registerDefaultTools();
    }

    public AtlasRuntime(
            Context context,
            String workingDirectory
    ) {
        this.shell = new ShellCommandExecutor(workingDirectory);

        this.toolRegistry = new ToolRegistry();
        this.policyEngine = new PolicyEngine();
        this.permissionResolver = new PermissionResolver();
        registerDefaultTools();

        if (context != null) {
            toolRegistry.register(
                    new AppsTool(context)
            );

            toolRegistry.register(
                    new ScreenTool(context)
            );
        }
    }

    private void registerDefaultTools() {
        toolRegistry.register(new ControlTool());
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

    public ToolResult executeTool(
            String toolName,
            String action,
            String... args
    ) {
        PolicyDecision decision = policyEngine.evaluate(
                new PolicyRequest(toolName, action, args)
        );

        if (decision == PolicyDecision.DENY) {
            return ToolResult.failure(
                    "Policy denied",
                    -10
            );
        }

        if (decision == PolicyDecision.REQUIRE_PERMISSION) {
            PermissionType permissionType =
                    permissionResolver.resolve(toolName, action);

            return ToolResult.failure(
                    "Permission required: "
                            + permissionType.name()
                            + " ("
                            + toolName.trim()
                            + "/"
                            + action.trim()
                            + ")",
                    -11
            );
        }

        AtlasTool tool = toolRegistry.get(toolName);

        if (tool == null) {
            return ToolResult.failure(
                    "Tool not found: " + toolName,
                    -1
            );
        }

        return tool.execute(action, args);
    }

    public AtlasTool getTool(String toolName) {
        return toolRegistry.get(toolName);
    }

    public ToolRegistry getToolRegistry() {
        return toolRegistry;
    }

    public String getWorkingDirectory() {
        return shell.getWorkingDirectory();
    }
}
