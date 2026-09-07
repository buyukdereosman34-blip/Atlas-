package com.atlas.app.runtime;

import com.atlas.app.tool.ToolResult;

import org.junit.Test;

import static org.junit.Assert.*;

public class AtlasRuntimeTest {

    @Test
    public void controlToolIsRegisteredAndReady() {
        AtlasRuntime runtime = new AtlasRuntime(".");

        assertTrue(runtime.getToolRegistry().contains("control"));

        ToolResult result =
                runtime.executeTool("control", "status");

        assertTrue(result.success);
        assertEquals("ControlTool: ready", result.output);
        assertEquals(0, result.code);
    }

    @Test
    public void unknownToolReturnsFailure() {
        AtlasRuntime runtime = new AtlasRuntime(".");

        ToolResult result =
                runtime.executeTool("does-not-exist", "status");

        assertFalse(result.success);
        assertEquals(-1, result.code);
    }

    @Test
    public void executeShellReturnsOutput() {
        AtlasRuntime runtime = new AtlasRuntime(".");

        ToolResult result =
                runtime.executeShell("printf 'atlas-shell-ok'");

        assertTrue(result.success);
        assertEquals("atlas-shell-ok", result.output);
        assertEquals(0, result.code);
    }

    @Test
    public void executeShellReturnsFailureForNonZeroExit() {
        AtlasRuntime runtime = new AtlasRuntime(".");

        ToolResult result =
                runtime.executeShell("exit 7");

        assertFalse(result.success);
        assertEquals(7, result.code);
    }
}
