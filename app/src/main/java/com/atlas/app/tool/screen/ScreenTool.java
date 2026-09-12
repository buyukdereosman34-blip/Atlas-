package com.atlas.app.tool.screen;

import android.content.Context;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.atlas.app.tool.AtlasTool;
import com.atlas.app.tool.ToolResult;

public class ScreenTool implements AtlasTool {

    private final Context context;

    public ScreenTool(Context context) {
        this.context = context != null
                ? context.getApplicationContext()
                : null;
    }

    @Override
    public String getName() {
        return "screen";
    }

    @Override
    public String getDescription() {
        return "Android ekran bilgilerini ve ekran yakalama işlemlerini yönetme";
    }

    @Override
    public ToolResult execute(String action, String... args) {
        if (action == null || action.trim().isEmpty()) {
            return ToolResult.failure("Screen action is empty", -1);
        }

        String normalized = action.trim().toLowerCase();

        switch (normalized) {
            case "status":
                return ToolResult.success("ScreenTool: ready");

            case "info":
                return getScreenInfo();

            default:
                return ToolResult.failure(
                        "Unsupported screen action: " + action,
                        -2
                );
        }
    }

    private ToolResult getScreenInfo() {
        if (context == null) {
            return ToolResult.failure(
                    "ScreenTool requires Android context",
                    -4
            );
        }

        try {
            android.util.DisplayMetrics metrics =
                    context.getResources().getDisplayMetrics();

            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            float density = metrics.density;
            int dpi = metrics.densityDpi;

            WindowManager windowManager =
                    (WindowManager) context.getSystemService(
                            Context.WINDOW_SERVICE
                    );

            if (windowManager == null) {
                return ToolResult.failure(
                        "WindowManager unavailable",
                        -3
                );
            }

            Display display = windowManager.getDefaultDisplay();

            float refreshRate =
                    display != null
                            ? display.getRefreshRate()
                            : 0f;

            int rotation =
                    display != null
                            ? display.getRotation()
                            : Surface.ROTATION_0;

            String orientation;

            switch (rotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_180:
                    orientation = "Dikey";
                    break;

                case Surface.ROTATION_90:
                case Surface.ROTATION_270:
                    orientation = "Yatay";
                    break;

                default:
                    orientation = "Bilinmiyor";
                    break;
            }

            String output =
                    "Resolution: " + width + " × " + height + " px\n" +
                    "Density: " + String.format("%.2f", density) + "\n" +
                    "DPI: " + dpi + "\n" +
                    "Refresh rate: " + String.format("%.2f", refreshRate) + " Hz\n" +
                    "Orientation: " + orientation;

            return ToolResult.success(output);

        } catch (Exception e) {
            return ToolResult.failure(
                    "ScreenTool error: " + e.getMessage(),
                    -3
            );
        }
    }
}
