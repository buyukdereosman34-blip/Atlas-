package com.atlas.app.tool;

import org.junit.Test;

import static org.junit.Assert.*;

public class ToolRegistryTest {

    private static class TestTool implements AtlasTool {

        private final String name;

        TestTool(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return "Test tool";
        }

        @Override
        public ToolResult execute(String action, String... args) {
            return ToolResult.success("test-ok");
        }
    }

    @Test
    public void registerAndGetTool() {
        ToolRegistry registry = new ToolRegistry();
        AtlasTool tool = new TestTool("test");

        registry.register(tool);

        assertSame(tool, registry.get("test"));
        assertTrue(registry.contains("test"));
        assertEquals(1, registry.size());
    }

    @Test
    public void toolLookupIsCaseInsensitive() {
        ToolRegistry registry = new ToolRegistry();
        AtlasTool tool = new TestTool("TestTool");

        registry.register(tool);

        assertSame(tool, registry.get("testtool"));
        assertSame(tool, registry.get("TESTTOOL"));
        assertSame(tool, registry.get("TeStToOl"));
    }

    @Test
    public void unknownToolReturnsNull() {
        ToolRegistry registry = new ToolRegistry();

        assertNull(registry.get("does-not-exist"));
        assertFalse(registry.contains("does-not-exist"));
    }

    @Test
    public void nullToolIsIgnored() {
        ToolRegistry registry = new ToolRegistry();

        registry.register(null);

        assertEquals(0, registry.size());
    }

    @Test
    public void blankToolNameIsIgnored() {
        ToolRegistry registry = new ToolRegistry();

        registry.register(new TestTool("   "));

        assertEquals(0, registry.size());
    }
}
