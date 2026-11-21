package com.atlas.app.ai;

import com.atlas.app.runtime.ToolCall;
import com.atlas.app.tool.ToolRegistry;

import org.json.JSONObject;

public final class LLMOutputGuard {

    public static final class Guarded {
        public final boolean valid;
        public final String reason;
        public final ToolCall call;

        private Guarded(boolean valid, String reason, ToolCall call) {
            this.valid = valid;
            this.reason = reason;
            this.call = call;
        }

        public static Guarded ok(ToolCall c) { return new Guarded(true, "", c); }
        public static Guarded fail(String reason) { return new Guarded(false, reason, null); }
    }

    private final ToolRegistry registry;

    public LLMOutputGuard(ToolRegistry registry) {
        this.registry = registry;
    }

    public Guarded sanitize(String raw) {
        if (raw == null) return Guarded.fail("Raw output null");

        String cleaned = stripMarkdown(raw);
        cleaned = stripControlChars(cleaned);

        JSONObject obj;
        try {
            obj = new JSONObject(cleaned);
        } catch (Exception e) {
            return Guarded.fail("Invalid JSON: " + e.getMessage());
        }

        String tool = obj.optString("tool", "").trim();
        String action = obj.optString("action", "").trim();

        if (tool.isEmpty() || action.isEmpty()) return Guarded.fail("Missing tool/action");

        if (registry == null || !registry.contains(tool)) {
            return Guarded.fail("Unknown tool: " + tool);
        }

        ToolCall.Builder b = ToolCall.builder().tool(tool).action(action);

        JSONObject args = obj.optJSONObject("args");
        if (args != null) {
            java.util.Iterator<String> keys = args.keys();
            while (keys.hasNext()) {
                String k = keys.next();
                String v = String.valueOf(args.opt(k));
                if (v.contains("\n") || v.contains(";") || v.contains("&&")
                        || v.contains("|") || v.contains("$(")) {
                    return Guarded.fail("Dangerous arg value for key: " + k);
                }
                b.arg(k, v);
            }
        }

        if (obj.has("targetPackage")) b.targetPackage(obj.optString("targetPackage"));
        if (obj.has("screenStateHash")) b.screenStateHash(obj.optString("screenStateHash"));

        try {
            return Guarded.ok(b.build());
        } catch (Exception e) {
            return Guarded.fail("Build failed: " + e.getMessage());
        }
    }

    private String stripMarkdown(String s) {
        return s.replaceAll("```json", "").replaceAll("```", "").trim();
    }

    private String stripControlChars(String s) {
        return s.replaceAll("[\\p{Cntrl}&&[^\n\t]]", "");
    }
}
