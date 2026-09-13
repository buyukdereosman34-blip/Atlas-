package com.atlas.app.permission;

public class PermissionChecker {

    public PermissionState check(PermissionType permissionType) {
        if (permissionType == null) {
            return PermissionState.UNKNOWN;
        }

        if (permissionType == PermissionType.NONE) {
            return PermissionState.AVAILABLE;
        }

        return PermissionState.UNKNOWN;
    }
}
