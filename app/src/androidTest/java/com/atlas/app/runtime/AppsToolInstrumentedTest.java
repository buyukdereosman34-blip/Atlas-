package com.atlas.app.runtime;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.atlas.app.tool.ToolResult;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AppsToolInstrumentedTest {

    @Test
    public void appsToolWorksWithRealAndroidContext() {
        Context context =
                ApplicationProvider.getApplicationContext();

        AtlasRuntime runtime =
                new AtlasRuntime(
                        context,
                        context.getFilesDir().getAbsolutePath()
                );

        assertTrue(
                runtime.getToolRegistry().contains("apps")
        );

        ToolResult status =
                runtime.executeTool("apps", "status");

        assertTrue(status.success);
        assertEquals(
                "AppsTool: ready",
                status.output
        );
        assertEquals(0, status.code);

        ToolResult list =
                runtime.executeTool("apps", "list");

        assertTrue(list.success);
        assertTrue(
                list.output.startsWith(
                        "Installed applications:"
                )
        );
    }
}
