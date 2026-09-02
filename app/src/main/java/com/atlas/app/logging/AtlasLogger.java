package com.atlas.app.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AtlasLogger {

    public enum Level {
        INFO,
        WARN,
        ERROR,
        SYSTEM
    }

    public interface Listener {
        void onLog(String line);
    }

    private final Listener listener;
    private final LogBuffer buffer;

    public AtlasLogger(Listener listener) {
        this(listener, 500);
    }

    public AtlasLogger(Listener listener, int maxEntries) {
        this.listener = listener;
        this.buffer = new LogBuffer(maxEntries);
    }

    public void log(Level level, String message) {
        String time = new SimpleDateFormat(
            "HH:mm:ss",
            Locale.US
        ).format(new Date());

        String line =
            "[" + time + "] " +
            "[" + level.name() + "] " +
            message;

        buffer.add(line);

        if (listener != null) {
            listener.onLog(line);
        }
    }

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void warn(String message) {
        log(Level.WARN, message);
    }

    public void error(String message) {
        log(Level.ERROR, message);
    }

    public void system(String message) {
        log(Level.SYSTEM, message);
    }

    public LogBuffer getBuffer() {
        return buffer;
    }
}
