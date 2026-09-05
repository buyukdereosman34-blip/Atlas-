package com.atlas.app.tool;

public interface AtlasTool {

    String getName();

    String getDescription();

    ToolResult execute(String action, String... args);
}
