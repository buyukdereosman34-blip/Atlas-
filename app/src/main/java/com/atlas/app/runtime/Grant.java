package com.atlas.app.runtime;

import java.util.concurrent.atomic.AtomicBoolean;

public final class Grant {
    private final String grantId;
    private final String taskId;
    private final String canonicalArgsHash;
    private final String targetPath;
    private final String targetPackage;
    private final String screenStateHash;
    private final long issuedAt;
    private final long ttlMs;
    private final AtomicBoolean consumed = new AtomicBoolean(false);

    public Grant(String grantId, String taskId, String canonicalArgsHash,
                 String targetPath, String targetPackage,
                 String screenStateHash, long ttlMs) {
        this.grantId = grantId;
        this.taskId = taskId;
        this.canonicalArgsHash = canonicalArgsHash;
        this.targetPath = targetPath;
        this.targetPackage = targetPackage;
        this.screenStateHash = screenStateHash;
        this.issuedAt = System.currentTimeMillis();
        this.ttlMs = ttlMs;
    }

    public boolean matches(ToolCall call) {
        if (!canonicalArgsHash.equals(call.canonicalHash())) return false;
        if (targetPath != null && !targetPath.equals(call.targetPath())) return false;
        if (targetPackage != null && !targetPackage.equals(call.targetPackage())) return false;
        if (screenStateHash != null && !screenStateHash.equals(call.screenStateHash())) return false;
        return true;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - issuedAt > ttlMs;
    }

    public boolean tryConsume() {
        if (isExpired()) return false;
        return consumed.compareAndSet(false, true);
    }

    public String grantId() { return grantId; }
    public String taskId() { return taskId; }
}
