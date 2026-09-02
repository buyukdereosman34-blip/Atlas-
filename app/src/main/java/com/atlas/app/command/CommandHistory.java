package com.atlas.app.command;

import java.util.ArrayList;
import java.util.List;

public class CommandHistory {

    private final int maxEntries;
    private final List<String> entries = new ArrayList<>();
    private int position = 0;

    public CommandHistory(int maxEntries) {
        if (maxEntries <= 0) {
            throw new IllegalArgumentException(
                "maxEntries must be greater than zero"
            );
        }

        this.maxEntries = maxEntries;
    }

    public synchronized void add(String command) {
        if (command == null || command.trim().isEmpty()) {
            return;
        }

        String value = command.trim();

        if (!entries.isEmpty() &&
            entries.get(entries.size() - 1).equals(value)) {
            position = entries.size();
            return;
        }

        entries.add(value);

        while (entries.size() > maxEntries) {
            entries.remove(0);
        }

        position = entries.size();
    }

    public synchronized String previous() {
        if (entries.isEmpty()) {
            return "";
        }

        if (position > 0) {
            position--;
        }

        return entries.get(position);
    }

    public synchronized String next() {
        if (entries.isEmpty()) {
            return "";
        }

        if (position < entries.size() - 1) {
            position++;
            return entries.get(position);
        }

        position = entries.size();
        return "";
    }

    public synchronized void resetPosition() {
        position = entries.size();
    }

    public synchronized int size() {
        return entries.size();
    }
}
