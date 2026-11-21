package com.atlas.app.runtime;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ToolResult {
    public enum Status { SUCCESS, FAILURE, UNKNOWN, DENIED, APPROVAL_REQUIRED }
    public enum Source { SHIZUKU, ACCESSIBILITY, ADB, UIAUTOMATOR2, LOCAL, UNKNOWN }

    public final Status status;
    public final Source source;
    public final String output;
    public final int code;
    public final String evidenceHash;
    public final Map<String, String> metadata;

    private ToolResult(Status status, Source source, String output, int code,
                       String evidenceHash, Map<String, String> metadata) {
        this.status = status;
        this.source = source == null ? Source.UNKNOWN : source;
        this.output = output == null ? "" : output;
        this.code = code;
        this.evidenceHash = evidenceHash;
        this.metadata = metadata == null ? Collections.emptyMap()
            : Collections.unmodifiableMap(new LinkedHashMap<>(metadata));
    }

    public static ToolResult success(Source source, String output, Map<String, String> meta) {
        return new ToolResult(Status.SUCCESS, source, output, 0, null, meta);
    }
    public static ToolResult failure(Source source, String output, int code) {
        return new ToolResult(Status.FAILURE, source, output, code, null, null);
    }
    public static ToolResult unknown(Source source, String reason) {
        return new ToolResult(Status.UNKNOWN, source, reason, -1, null, null);
    }
    public static ToolResult denied(ToolCall call) {
        return new ToolResult(Status.DENIED, Source.LOCAL,
            "Denied: " + call.toolName() + "/" + call.action(), -10, null, null);
    }
    public static ToolResult approvalRequired(ToolCall call, String reason) {
        return new ToolResult(Status.APPROVAL_REQUIRED, Source.LOCAL, reason, -11, null, null);
    }
    public boolean isSuccess() { return status == Status.SUCCESS; }
}
