package com.atlas.app.policy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PolicyEngineTest {

    private final PolicyEngine engine = new PolicyEngine();

    @Test
    public void controlStatusIsAllowed() {
        PolicyRequest request =
                new PolicyRequest("control", "status");

        assertEquals(
                PolicyDecision.ALLOW,
                engine.evaluate(request)
        );
    }

    @Test
    public void toolAndActionAreCaseInsensitive() {
        PolicyRequest request =
                new PolicyRequest(" CONTROL ", " STATUS ");

        assertEquals(
                PolicyDecision.ALLOW,
                engine.evaluate(request)
        );
    }

    @Test
    public void appsStatusIsAllowed() {
        PolicyRequest request =
                new PolicyRequest("apps", "status");

        assertEquals(
                PolicyDecision.ALLOW,
                engine.evaluate(request)
        );
    }

    @Test
    public void appsListIsAllowed() {
        PolicyRequest request =
                new PolicyRequest("apps", "list");

        assertEquals(
                PolicyDecision.ALLOW,
                engine.evaluate(request)
        );
    }

    @Test
    public void screenStatusIsAllowed() {
        PolicyRequest request =
                new PolicyRequest("screen", "status");

        assertEquals(
                PolicyDecision.ALLOW,
                engine.evaluate(request)
        );
    }

    @Test
    public void screenInfoIsAllowed() {
        PolicyRequest request =
                new PolicyRequest("screen", "info");

        assertEquals(
                PolicyDecision.ALLOW,
                engine.evaluate(request)
        );
    }

    @Test
    public void unknownActionRequiresPermission() {
        PolicyRequest request =
                new PolicyRequest("control", "tap");

        assertEquals(
                PolicyDecision.REQUIRE_PERMISSION,
                engine.evaluate(request)
        );
    }

    @Test
    public void unknownToolRequiresPermission() {
        PolicyRequest request =
                new PolicyRequest("unknown", "status");

        assertEquals(
                PolicyDecision.REQUIRE_PERMISSION,
                engine.evaluate(request)
        );
    }

    @Test
    public void emptyToolIsDenied() {
        PolicyRequest request =
                new PolicyRequest("", "status");

        assertEquals(
                PolicyDecision.DENY,
                engine.evaluate(request)
        );
    }

    @Test
    public void emptyActionIsDenied() {
        PolicyRequest request =
                new PolicyRequest("control", "");

        assertEquals(
                PolicyDecision.DENY,
                engine.evaluate(request)
        );
    }

    @Test
    public void nullRequestIsDenied() {
        assertEquals(
                PolicyDecision.DENY,
                engine.evaluate(null)
        );
    }
}
