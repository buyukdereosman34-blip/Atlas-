package com.atlas.app.runtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TaskStore {

    public enum State {
        RUNNING, DONE, FAILED, NEEDS_USER, BUDGET_EXHAUSTED, ABORTED;

        public boolean isTerminal() { return this != RUNNING; }
    }

    public static final class TaskRecord {
        public final String taskId;
        public volatile State state = State.RUNNING;
        public volatile long startedAt = System.currentTimeMillis();
        public volatile long endedAt = 0L;
        public volatile String reason = "";
        public volatile String checkpointJson = "";

        public TaskRecord(String taskId) { this.taskId = taskId; }
    }

    private final Map<String, TaskRecord> tasks = new ConcurrentHashMap<>();

    public TaskRecord create(String taskId) {
        TaskRecord r = new TaskRecord(taskId);
        tasks.put(taskId, r);
        return r;
    }

    public TaskRecord get(String taskId) { return tasks.get(taskId); }

    public synchronized void finish(String taskId, State newState, String reason) {
        if (!newState.isTerminal()) throw new IllegalArgumentException("Not terminal: " + newState);
        TaskRecord r = tasks.get(taskId);
        if (r == null || r.state.isTerminal()) return;
        r.state = newState;
        r.endedAt = System.currentTimeMillis();
        r.reason = reason == null ? "" : reason;
    }

    public void saveCheckpoint(String taskId, String json) {
        TaskRecord r = tasks.get(taskId);
        if (r != null) r.checkpointJson = json;
    }

    public boolean isRunning(String taskId) {
        TaskRecord r = tasks.get(taskId);
        return r != null && r.state == State.RUNNING;
    }
}
