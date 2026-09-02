package com.atlas.app.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogBuffer {

    private final int maxEntries;
    private final List<String> entries = new ArrayList<>();

    public LogBuffer(int maxEntries) {
        if (maxEntries <= 0) {
            throw new IllegalArgumentException("maxEntries must be greater than zero");
        }

        this.maxEntries = maxEntries;
    }

    public synchronized void add(String line) {
        if (line == null) {
            return;
        }

        entries.add(line);

        while (entries.size() > maxEntries) {
            entries.remove(0);
        }
    }

    public synchronized List<String> snapshot() {
        return Collections.unmodifiableList(
            new ArrayList<>(entries)
        );
    }

    public synchronized void clear() {
        entries.clear();
    }

    public synchronized int size() {
        return entries.size();
    }
}
