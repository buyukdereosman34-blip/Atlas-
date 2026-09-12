package com.atlas.app.tool.control;

import com.atlas.app.tool.ToolResult;

import org.junit.Test;

import static org.junit.Assert.*;

public class ControlToolTest {

    @Test
    public void statusActionReturnsReady() {
        ControlTool tool = new ControlTool();

        ToolResult result = tool.execute("status");

        assertTrue(result.success);
        assertEquals("ControlTool: ready", result.output);
        assertEquals(0, result.code);
    }

    @Test
    public void actionIsNormalized() {
        ControlTool tool = new ControlTool();

        ToolResult result = tool.execute(" STATUS ");

        assertTrue(result.success);
        assertEquals("ControlTool: ready", result.output);
        assertEquals(0, result.code);
    }

    @Test
    public void emptyActionReturnsFailure() {
        ControlTool tool = new ControlTool();

        ToolResult result = tool.execute("   ");

        assertFalse(result.success);
        assertEquals("Control action is empty", result.output);
        assertEquals(-1, result.code);
    }

    @Test
    public void unsupportedActionReturnsFailure() {
        ControlTool tool = new ControlTool();

        ToolResult result = tool.execute("tap");

        assertFalse(result.success);
        assertEquals("Unsupported control action: tap", result.output);
        assertEquals(-2, result.code);
    }

    @Test
    public void nullActionReturnsFailure() {
        ControlTool tool = new ControlTool();

        ToolResult result = tool.execute(null);

        assertFalse(result.success);
        assertEquals("Control action is empty", result.output);
        assertEquals(-1, result.code);
    }
}
