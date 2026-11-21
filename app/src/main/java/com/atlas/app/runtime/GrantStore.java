package com.atlas.app.runtime;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class GrantStore {

    private static final long DEFAULT_TTL_MS = 90_000L;

    private final Map<String, Grant> active = new ConcurrentHashMap<>();
    private final EventLedger ledger;

    public GrantStore(EventLedger ledger) {
        this.ledger = ledger;
    }

    public Grant issue(String taskId, ToolCall call) {
        String id = "grant-" + UUID.randomUUID();
        Grant g = new Grant(id, taskId, call.canonicalHash(),
            call.targetPath(), call.targetPackage(),
            call.screenStateHash(), DEFAULT_TTL_MS);
        active.put(id, g);
        return g;
    }

    public Grant tryConsume(String taskId, ToolCall call) {
        for (Grant g : active.values()) {
            if (!taskId.equals(g.taskId())) continue;
            if (!g.matches(call)) continue;
            if (g.isExpired()) {
                active.remove(g.grantId());
                continue;
            }
            if (g.tryConsume()) {
                active.remove(g.grantId());
                ledger.writeGrantConsumed(taskId, g.grantId());
                return g;
            }
        }
        return null;
    }

    public void revokeTask(String taskId) {
        active.values().removeIf(g -> taskId.equals(g.taskId()));
    }

    public void revokeAll() { active.clear(); }
}
