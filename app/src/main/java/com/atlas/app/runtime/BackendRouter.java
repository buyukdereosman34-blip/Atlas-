package com.atlas.app.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class BackendRouter {
    private final CapabilityRegistry registry;

    public BackendRouter(CapabilityRegistry registry) {
        this.registry = registry;
    }

    public ToolResult route(ToolCall call) {
        CapabilityBackend.Capability cap = mapCapability(call);
        if (cap == null) {
//             return ToolResult.failure(ToolResult.Source.LOCAL, "Unknown capability for " + call.toolName(), -1);
        return null; // Legacy fallback
        }

        List<CapabilityBackend> candidates = new ArrayList<>();
        for (CapabilityBackend b : registry.all()) {
            if (b.health() == CapabilityBackend.Health.DOWN) continue;
            if (!b.capabilities().contains(cap)) continue;
            candidates.add(b);
        }

        if (candidates.isEmpty()) return null;

        Collections.sort(candidates, Comparator
            .comparingInt((CapabilityBackend b) -> b.authority().ordinal())
            .thenComparing(Comparator.comparingDouble(CapabilityBackend::reliability).reversed())
            .thenComparingLong(CapabilityBackend::latencyMs));

        ToolResult last = null;
        for (CapabilityBackend b : candidates) {
            ToolResult r = b.execute(call);
            if (r.status == ToolResult.Status.SUCCESS) return r;
            if (r.status == ToolResult.Status.DENIED) return r;
            if (r.status == ToolResult.Status.APPROVAL_REQUIRED) return r;
            last = r;
        }
        return last;
    }

    private CapabilityBackend.Capability mapCapability(ToolCall call) {
        String t = call.toolName().toLowerCase();
        String a = call.action().toLowerCase();
        if (t.equals("control")) {
            switch (a) {
                case "tap": return CapabilityBackend.Capability.TAP;
                case "swipe": return CapabilityBackend.Capability.SWIPE;
                case "text": case "type": return CapabilityBackend.Capability.TEXT;
                case "key": return CapabilityBackend.Capability.KEY;
            }
        }
        if (t.equals("screen")) {
            if (a.equals("tree")) return CapabilityBackend.Capability.TREE;
            if (a.equals("screenshot")) return CapabilityBackend.Capability.SCREENSHOT;
        }
        if (t.equals("shell")) return CapabilityBackend.Capability.SHELL;
        return null;
    }
}
