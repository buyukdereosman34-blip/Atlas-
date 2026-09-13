package com.atlas.app.permission;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PermissionResolverTest {

    private final PermissionResolver resolver = new PermissionResolver();

    @Test
    public void controlStatusRequiresNoPermission() {
        assertEquals(
                PermissionType.NONE,
                resolver.resolve("control", "status")
        );
    }

    @Test
    public void controlTapRequiresInputControl() {
        assertEquals(
                PermissionType.INPUT_CONTROL,
                resolver.resolve("control", "tap")
        );
    }

    @Test
    public void screenInfoRequiresNoPermission() {
        assertEquals(
                PermissionType.NONE,
                resolver.resolve("screen", "info")
        );
    }

    @Test
    public void screenCaptureRequiresScreenCapture() {
        assertEquals(
                PermissionType.SCREEN_CAPTURE,
                resolver.resolve("screen", "capture")
        );
    }

    @Test
    public void appsListRequiresNoPermission() {
        assertEquals(
                PermissionType.NONE,
                resolver.resolve("apps", "list")
        );
    }

    @Test
    public void unknownToolIsUnknown() {
        assertEquals(
                PermissionType.UNKNOWN,
                resolver.resolve("unknown", "status")
        );
    }

    @Test
    public void unknownActionIsUnknown() {
        assertEquals(
                PermissionType.UNKNOWN,
                resolver.resolve("apps", "unknown")
        );
    }

    @Test
    public void emptyToolIsUnknown() {
        assertEquals(
                PermissionType.UNKNOWN,
                resolver.resolve("", "status")
        );
    }

    @Test
    public void nullRequestIsUnknown() {
        assertEquals(
                PermissionType.UNKNOWN,
                resolver.resolve(null, "status")
        );
    }

    @Test
    public void namesAreCaseInsensitive() {
        assertEquals(
                PermissionType.INPUT_CONTROL,
                resolver.resolve("CONTROL", "TAP")
        );
    }
}
