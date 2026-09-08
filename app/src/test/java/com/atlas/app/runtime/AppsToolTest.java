package com.atlas.app.runtime;

import com.atlas.app.tool.ToolResult;
import com.atlas.app.tool.apps.AppsTool;

import org.junit.Test;

import static org.junit.Assert.*;

public class AppsToolTest {

    @Test
    public void appsToolHasCorrectMetadata() {
        AppsTool tool = new AppsTool(null);

        assertEquals("apps", tool.getName());
        assertEquals(
                "Yüklü Android uygulamalarını listeleme ve inceleme işlemleri",
                tool.getDescription()
        );
    }

    @Test
    public void appsToolStatusIsReady() {
        AppsTool tool = new AppsTool(null);

        ToolResult result =
                tool.execute("status");

        assertTrue(result.success);
        assertEquals(
                "AppsTool: ready",
                result.output
        );
        assertEquals(0, result.code);
    }

    @Test
    public void unsupportedActionReturnsFailure() {
        AppsTool tool = new AppsTool(null);

        ToolResult result =
                tool.execute("does-not-exist");

        assertFalse(result.success);
        assertEquals(-2, result.code);
    }
}
