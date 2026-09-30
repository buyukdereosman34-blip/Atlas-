package com.atlas.app.tool;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ToolResult {

    public final boolean success;
    public final String output;
    public final int code;

    public final String riskLevel;
    public final String artifactPath;
    public final boolean requiresApproval;
    public final String nextHint;
    public final Map<String, String> metadata;

    private ToolResult(
            boolean success,
            String output,
            int code,
            String riskLevel,
            String artifactPath,
            boolean requiresApproval,
            String nextHint,
            Map<String, String> metadata
    ) {
        this.success = success;
        this.output = output;
        this.code = code;
        this.riskLevel = riskLevel;
        this.artifactPath = artifactPath;
        this.requiresApproval = requiresApproval;
        this.nextHint = nextHint;

        if (metadata == null || metadata.isEmpty()) {
            this.metadata = Collections.emptyMap();
        } else {
            this.metadata = Collections.unmodifiableMap(
                    new LinkedHashMap<>(metadata)
            );
        }
    }

    public static ToolResult success(String output) {
        return new ToolResult(
                true,
                output,
                0,
                "UNKNOWN",
                null,
                false,
                null,
                null
        );
    }

    public static ToolResult failure(
            String output,
            int code
    ) {
        return new ToolResult(
                false,
                output,
                code,
                "UNKNOWN",
                null,
                false,
                null,
                null
        );
    }

    public static ToolResult observation(
            boolean success,
            String output,
            int code,
            String riskLevel,
            String artifactPath,
            boolean requiresApproval,
            String nextHint,
            Map<String, String> metadata
    ) {
        return new ToolResult(
                success,
                output,
                code,
                riskLevel,
                artifactPath,
                requiresApproval,
                nextHint,
                metadata
        );
    }
}
