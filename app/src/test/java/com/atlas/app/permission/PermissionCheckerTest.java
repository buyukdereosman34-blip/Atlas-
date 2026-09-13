package com.atlas.app.permission;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PermissionCheckerTest {

    private final PermissionChecker checker =
            new PermissionChecker();

    @Test
    public void noPermissionIsAvailable() {
        assertEquals(
                PermissionState.AVAILABLE,
                checker.check(PermissionType.NONE)
        );
    }

    @Test
    public void inputControlIsCurrentlyUnknown() {
        assertEquals(
                PermissionState.UNKNOWN,
                checker.check(PermissionType.INPUT_CONTROL)
        );
    }

    @Test
    public void screenCaptureIsCurrentlyUnknown() {
        assertEquals(
                PermissionState.UNKNOWN,
                checker.check(PermissionType.SCREEN_CAPTURE)
        );
    }

    @Test
    public void unknownPermissionIsUnknown() {
        assertEquals(
                PermissionState.UNKNOWN,
                checker.check(PermissionType.UNKNOWN)
        );
    }

    @Test
    public void nullPermissionIsUnknown() {
        assertEquals(
                PermissionState.UNKNOWN,
                checker.check(null)
        );
    }
}
