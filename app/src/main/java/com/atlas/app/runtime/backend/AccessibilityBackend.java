package com.atlas.app.runtime.backend;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;

import com.atlas.app.runtime.CapabilityBackend;
import com.atlas.app.runtime.ToolCall;
import com.atlas.app.runtime.ToolResult;

import java.util.EnumSet;
import java.util.Set;

public final class AccessibilityBackend implements CapabilityBackend {

    private final AccessibilityService service;
    private final AccessibilityTreeParser treeParser = new AccessibilityTreeParser();

    public AccessibilityBackend(AccessibilityService service) {
        this.service = service;
    }

    @Override public String name() { return "accessibility"; }
    @Override public AuthorityLevel authority() { return AuthorityLevel.A11Y_GESTURE; }
    @Override public Set<Capability> capabilities() {
        return EnumSet.of(Capability.TAP, Capability.SWIPE, Capability.TEXT, Capability.KEY, Capability.TREE);
    }
    @Override public Health health() { return service == null ? Health.DOWN : Health.AVAILABLE; }
    @Override public double reliability() { return 0.85; }
    @Override public long latencyMs() { return 80; }

    @Override
    public ToolResult execute(ToolCall call) {
        if (service == null) {
            return ToolResult.failure(ToolResult.Source.ACCESSIBILITY, "Accessibility service not attached", -1);
        }
        String action = call.action().toLowerCase();
        try {
            switch (action) {
                case "tap": return doTap(call);
                case "swipe": return doSwipe(call);
                case "text":
                case "type": return doType(call);
                case "tree": return doTree();
                default:
                    return ToolResult.failure(ToolResult.Source.ACCESSIBILITY,
                        "Unsupported a11y action: " + action, -2);
            }
        } catch (Exception e) {
            return ToolResult.failure(ToolResult.Source.ACCESSIBILITY, "A11y error: " + e.getMessage(), -3);
        }
    }

    private ToolResult doTap(ToolCall call) {
        float x = Float.parseFloat(call.args().getOrDefault("x", "0"));
        float y = Float.parseFloat(call.args().getOrDefault("y", "0"));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return ToolResult.failure(ToolResult.Source.ACCESSIBILITY, "dispatchGesture needs API 24+", -4);
        }
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, 100);
        GestureDescription gesture = new GestureDescription.Builder().addStroke(stroke).build();
        final boolean[] ok = {false};
        service.dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
            @Override public void onCompleted(GestureDescription g) { ok[0] = true; }
            @Override public void onCancelled(GestureDescription g) { ok[0] = false; }
        }, null);
        if (ok[0]) return ToolResult.success(ToolResult.Source.ACCESSIBILITY, "tap(" + x + "," + y + ")", null);
        return ToolResult.failure(ToolResult.Source.ACCESSIBILITY, "tap cancelled", -5);
    }

    private ToolResult doSwipe(ToolCall call) {
        float x1 = Float.parseFloat(call.args().getOrDefault("x1", "0"));
        float y1 = Float.parseFloat(call.args().getOrDefault("y1", "0"));
        float x2 = Float.parseFloat(call.args().getOrDefault("x2", "0"));
        float y2 = Float.parseFloat(call.args().getOrDefault("y2", "0"));
        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, 300);
        GestureDescription gesture = new GestureDescription.Builder().addStroke(stroke).build();
        service.dispatchGesture(gesture, null, null);
        return ToolResult.success(ToolResult.Source.ACCESSIBILITY,
            "swipe(" + x1 + "," + y1 + " → " + x2 + "," + y2 + ")", null);
    }

    private ToolResult doType(ToolCall call) {
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        if (root == null) return ToolResult.failure(ToolResult.Source.ACCESSIBILITY, "No active window", -6);
        AccessibilityNodeInfo focused = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (focused == null) {
            root.recycle();
            return ToolResult.failure(ToolResult.Source.ACCESSIBILITY, "No focused input", -7);
        }
        android.os.Bundle args = new android.os.Bundle();
        args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            call.args().getOrDefault("text", ""));
        boolean ok = focused.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
        focused.recycle();
        root.recycle();
        if (ok) return ToolResult.success(ToolResult.Source.ACCESSIBILITY, "text set", null);
        return ToolResult.failure(ToolResult.Source.ACCESSIBILITY, "text set failed", -8);
    }

    private ToolResult doTree() {
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        if (root == null) return ToolResult.failure(ToolResult.Source.ACCESSIBILITY, "No active window", -9);
        java.util.List<AccessibilityTreeParser.ParsedNode> nodes = treeParser.parse(root);
        root.recycle();
        String prompt = treeParser.toPrompt(nodes);
        return ToolResult.success(ToolResult.Source.ACCESSIBILITY, prompt, null);
    }
}
