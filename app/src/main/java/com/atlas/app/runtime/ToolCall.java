package com.atlas.app.runtime;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public final class ToolCall {
    private final String toolName;
    private final String action;
    private final Map<String, String> args;
    private final String targetPath;
    private final String targetPackage;
    private final String screenStateHash;
    private final long requestedAt;

    private ToolCall(Builder b) {
        this.toolName = b.toolName;
        this.action = b.action;
        this.args = Collections.unmodifiableMap(new TreeMap<>(b.args));
        this.targetPath = b.targetPath;
        this.targetPackage = b.targetPackage;
        this.screenStateHash = b.screenStateHash;
        this.requestedAt = b.requestedAt;
    }

    public static Builder builder() { return new Builder(); }
    public String toolName() { return toolName; }
    public String action() { return action; }
    public Map<String, String> args() { return args; }
    public String targetPath() { return targetPath; }
    public String targetPackage() { return targetPackage; }
    public String screenStateHash() { return screenStateHash; }
    public long requestedAt() { return requestedAt; }

    public String canonicalHash() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(toolName).append('|').append(action).append('|')
              .append(targetPath == null ? "" : targetPath).append('|')
              .append(targetPackage == null ? "" : targetPackage).append('|')
              .append(screenStateHash == null ? "" : screenStateHash);
            for (Map.Entry<String, String> e : args.entrySet()) {
                sb.append('|').append(e.getKey()).append('=').append(e.getValue());
            }
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] h = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : h) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hash failed", e);
        }
    }

    public static final class Builder {
        private String toolName;
        private String action;
        private final Map<String, String> args = new LinkedHashMap<>();
        private String targetPath;
        private String targetPackage;
        private String screenStateHash;
        private long requestedAt = System.currentTimeMillis();

        public Builder tool(String name) { this.toolName = name; return this; }
        public Builder action(String a) { this.action = a; return this; }
        public Builder arg(String k, String v) { args.put(k, v); return this; }
        public Builder targetPath(String p) { this.targetPath = p; return this; }
        public Builder targetPackage(String p) { this.targetPackage = p; return this; }
        public Builder screenStateHash(String h) { this.screenStateHash = h; return this; }
        public Builder requestedAt(long t) { this.requestedAt = t; return this; }

        public ToolCall build() {
            if (toolName == null || toolName.trim().isEmpty()) throw new IllegalArgumentException("toolName required");
            if (action == null || action.trim().isEmpty()) throw new IllegalArgumentException("action required");
            return new ToolCall(this);
        }
    }
}
