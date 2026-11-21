package com.atlas.app.runtime;

import java.util.concurrent.CopyOnWriteArrayList;

public final class EventLedger {

    public enum Kind { INTENT, RESULT, APPROVAL, GRANT_CONSUMED, ERROR }

    public static final class Event {
        public final long ts;
        public final Kind kind;
        public final String taskId;
        public final String payload;

        public Event(long ts, Kind kind, String taskId, String payload) {
            this.ts = ts;
            this.kind = kind;
            this.taskId = taskId;
            this.payload = payload;
        }
    }

    private final CopyOnWriteArrayList<Event> events = new CopyOnWriteArrayList<>();
    private volatile boolean closed = false;

    public synchronized void writeIntent(String taskId, ToolCall call) {
        if (closed) throw new IllegalStateException("Ledger closed");
        events.add(new Event(System.currentTimeMillis(), Kind.INTENT, taskId,
            call.toolName() + "/" + call.action() + "|" + call.canonicalHash()));
    }

    public synchronized void writeResult(String taskId, ToolCall call, ToolResult result) {
        if (closed) throw new IllegalStateException("Ledger closed");
        events.add(new Event(System.currentTimeMillis(), Kind.RESULT, taskId,
            result.status.name() + "|" + result.source.name() + "|" + call.canonicalHash()));
    }

    public synchronized void writeApproval(String taskId, String grantId) {
        events.add(new Event(System.currentTimeMillis(), Kind.APPROVAL, taskId, grantId));
    }

    public synchronized void writeGrantConsumed(String taskId, String grantId) {
        events.add(new Event(System.currentTimeMillis(), Kind.GRANT_CONSUMED, taskId, grantId));
    }

    public synchronized void writeError(String taskId, String message) {
        events.add(new Event(System.currentTimeMillis(), Kind.ERROR, taskId, message));
    }

    public int size() { return events.size(); }
    public void close() { closed = true; }

    public java.util.List<Event> tail(int n) {
        int from = Math.max(0, events.size() - n);
        return new java.util.ArrayList<>(events.subList(from, events.size()));
    }
}
