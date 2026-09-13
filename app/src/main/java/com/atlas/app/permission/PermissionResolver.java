package com.atlas.app.permission;

public class PermissionResolver {

    public PermissionType resolve(
            String toolName,
            String action
    ) {
        if (toolName == null || action == null) {
            return PermissionType.UNKNOWN;
        }

        String normalizedTool = toolName.trim();
        String normalizedAction = action.trim();

        if (normalizedTool.isEmpty() || normalizedAction.isEmpty()) {
            return PermissionType.UNKNOWN;
        }

        if ("control".equalsIgnoreCase(normalizedTool)) {
            if ("status".equalsIgnoreCase(normalizedAction)) {
                return PermissionType.NONE;
            }

            return PermissionType.INPUT_CONTROL;
        }

        if ("screen".equalsIgnoreCase(normalizedTool)) {
            if ("status".equalsIgnoreCase(normalizedAction)
                    || "info".equalsIgnoreCase(normalizedAction)) {
                return PermissionType.NONE;
            }

            if ("capture".equalsIgnoreCase(normalizedAction)) {
                return PermissionType.SCREEN_CAPTURE;
            }

            return PermissionType.UNKNOWN;
        }

        if ("apps".equalsIgnoreCase(normalizedTool)) {
            if ("status".equalsIgnoreCase(normalizedAction)
                    || "list".equalsIgnoreCase(normalizedAction)) {
                return PermissionType.NONE;
            }

            return PermissionType.UNKNOWN;
        }

        return PermissionType.UNKNOWN;
    }
}
