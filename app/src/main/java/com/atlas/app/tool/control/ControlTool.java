package com.atlas.app.tool.control;

import com.atlas.app.tool.AtlasTool;
import com.atlas.app.tool.ToolResult;

public class ControlTool implements AtlasTool {

    @Override
    public String getName() {
        return "control";
    }

    @Override
    public String getDescription() {
        return "Android kontrol işlemleri";
    }

    @Override
    public ToolResult execute(
            String action,
            String... args
    ) {

        if (action == null || action.trim().isEmpty()) {
            return ToolResult.failure(
                    "Control action is empty",
                    -1
            );
        }

        String normalized =
                action.trim().toLowerCase();

        switch (normalized) {

            case "status":
                return ToolResult.success(
                        "ControlTool: ready"
                );

            default:
                return ToolResult.failure(
                        "Unsupported control action: "
                                + action,
                        -2
                );
        }
    }
}
