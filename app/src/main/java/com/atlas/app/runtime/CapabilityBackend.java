package com.atlas.app.runtime;

import java.util.Set;

public interface CapabilityBackend {
    enum Capability { TAP, SWIPE, TEXT, KEY, SCREENSHOT, TREE, SHELL, INPUT }
    enum AuthorityLevel { A11Y_NODE, A11Y_GESTURE, SHIZUKU, ADB, SHELL }
    enum Health { AVAILABLE, DEGRADED, DOWN }

    String name();
    AuthorityLevel authority();
    Set<Capability> capabilities();
    Health health();
    double reliability();
    long latencyMs();
    ToolResult execute(ToolCall call);
}
