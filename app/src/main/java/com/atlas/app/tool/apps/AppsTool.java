package com.atlas.app.tool.apps;

import android.content.Context;

import com.atlas.app.AppsController;
import com.atlas.app.tool.AtlasTool;
import com.atlas.app.tool.ToolResult;

import java.util.List;

public class AppsTool implements AtlasTool {

    private final Context context;

    public AppsTool(Context context) {
        this.context = context != null
                ? context.getApplicationContext()
                : null;
    }

    @Override
    public String getName() {
        return "apps";
    }

    @Override
    public String getDescription() {
        return "Yüklü Android uygulamalarını listeleme ve inceleme işlemleri";
    }

    @Override
    public ToolResult execute(String action, String... args) {
        if (action == null || action.trim().isEmpty()) {
            return ToolResult.failure("Apps action is empty", -1);
        }

        String normalized = action.trim().toLowerCase();

        switch (normalized) {
            case "status":
                return ToolResult.success("AppsTool: ready");

            case "list":
                return listApps();

            default:
                return ToolResult.failure(
                        "Unsupported apps action: " + action,
                        -2
                );
        }
    }

    private ToolResult listApps() {
        if (context == null) {
            return ToolResult.failure(
                    "AppsTool requires Android context",
                    -4
            );
        }

        try {
            AppsController controller =
                    new AppsController(context);

            List<AppsController.AppInfo> apps =
                    controller.getInstalledApps();

            StringBuilder output = new StringBuilder();

            output.append("Installed applications: ")
                    .append(apps.size())
                    .append("\n");

            for (AppsController.AppInfo app : apps) {
                output.append(app.getLabel())
                        .append(" | ")
                        .append(app.getPackageName())
                        .append(" | ")
                        .append(app.isSystemApp()
                                ? "system"
                                : "user")
                        .append("\n");
            }

            return ToolResult.success(
                    output.toString().trim()
            );

        } catch (Exception e) {
            return ToolResult.failure(
                    "AppsTool error: " + e.getMessage(),
                    -3
            );
        }
    }
}
