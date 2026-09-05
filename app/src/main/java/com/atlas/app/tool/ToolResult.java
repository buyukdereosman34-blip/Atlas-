package com.atlas.app.tool;

public class ToolResult {

    public final boolean success;
    public final String output;
    public final int code;

    private ToolResult(
            boolean success,
            String output,
            int code
    ) {
        this.success = success;
        this.output = output;
        this.code = code;
    }

    public static ToolResult success(String output) {
        return new ToolResult(
                true,
                output,
                0
        );
    }

    public static ToolResult failure(
            String output,
            int code
    ) {
        return new ToolResult(
                false,
                output,
                code
        );
    }
}
