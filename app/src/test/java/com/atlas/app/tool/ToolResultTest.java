package com.atlas.app.tool;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ToolResultTest {

    @Test
    public void legacySuccessFactoryRemainsCompatible() {
        ToolResult result = ToolResult.success("ok");

        assertTrue(result.success);
        assertEquals("ok", result.output);
        assertEquals(0, result.code);
        assertEquals("UNKNOWN", result.riskLevel);
        assertNull(result.artifactPath);
        assertFalse(result.requiresApproval);
        assertNull(result.nextHint);
        assertTrue(result.metadata.isEmpty());
    }

    @Test
    public void legacyFailureFactoryRemainsCompatible() {
        ToolResult result = ToolResult.failure("failed", -1);

        assertFalse(result.success);
        assertEquals("failed", result.output);
        assertEquals(-1, result.code);
        assertEquals("UNKNOWN", result.riskLevel);
        assertFalse(result.requiresApproval);
    }

    @Test
    public void observationFactoryStoresObservationData() {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("tool", "control");

        ToolResult result = ToolResult.observation(
                true,
                "completed",
                0,
                "LOW",
                "/tmp/result.txt",
                false,
                "continue",
                metadata
        );

        assertTrue(result.success);
        assertEquals("completed", result.output);
        assertEquals(0, result.code);
        assertEquals("LOW", result.riskLevel);
        assertEquals("/tmp/result.txt", result.artifactPath);
        assertFalse(result.requiresApproval);
        assertEquals("continue", result.nextHint);
        assertEquals("control", result.metadata.get("tool"));
    }
}
