package com.atlas.app.runtime;

import com.atlas.app.permission.PermissionChecker;
import com.atlas.app.permission.PermissionResolver;
import com.atlas.app.permission.PermissionState;
import com.atlas.app.permission.PermissionType;
import com.atlas.app.policy.PolicyDecision;
import com.atlas.app.policy.PolicyEngine;
import com.atlas.app.policy.PolicyRequest;
import com.atlas.app.tool.AtlasTool;
import com.atlas.app.tool.ToolRegistry;

/**
 * YENİ NESİL ÇALIŞTIRICI (P0).
 *
 * R1 INVARIANT: TEK GİRİŞ NOKTASI — bu sınıfın dışından tool çağrılamaz.
 * R3 INVARIANT: Grant binding (screen-hash dahil).
 * R4 INVARIANT: Write-ahead audit.
 * R6 INVARIANT: Capability-based routing.
 * R8 INVARIANT: Terminal states.
 *
 * Mevcut AtlasRuntime'ı BOZMAZ. Paralel çalışır.
 * Yarın: AtlasRuntime.executeTool() -> AtlasExecutor.execute() üzerinden yönlendirilir.
 */
public final class AtlasExecutor {

    private final ToolRegistry toolRegistry;
    private final PolicyEngine policyEngine;
    private final PermissionResolver permissionResolver;
    private final PermissionChecker permissionChecker;

    // P0 altyapı
    private final EventLedger ledger;
    private final GrantStore grantStore;
    private final TaskStore taskStore;
    private final CapabilityRegistry capabilityRegistry;
    private final BackendRouter backendRouter;

    public AtlasExecutor(ToolRegistry toolRegistry) {
        this(toolRegistry,
             new PolicyEngine(),
             new PermissionResolver(),
             new PermissionChecker());
    }

    public AtlasExecutor(ToolRegistry toolRegistry,
                         PolicyEngine policyEngine,
                         PermissionResolver permissionResolver,
                         PermissionChecker permissionChecker) {
        this.toolRegistry = toolRegistry;
        this.policyEngine = policyEngine;
        this.permissionResolver = permissionResolver;
        this.permissionChecker = permissionChecker;

        this.ledger = new EventLedger();
        this.grantStore = new GrantStore(ledger);
        this.taskStore = new TaskStore();
        this.capabilityRegistry = new CapabilityRegistry();
        this.backendRouter = new BackendRouter(capabilityRegistry);
        // A11y backend otomatik register
        try {
            com.atlas.app.runtime.backend.AccessibilityServiceBridge a11y =
                com.atlas.app.runtime.backend.AccessibilityServiceBridge.get();
            if (a11y != null) {
                capabilityRegistry.register(a11y.asBackend());
            }
        } catch (Throwable ignored) {}
    }

    /** Yeni task başlat. */
    public TaskStore.TaskRecord startTask(String taskId) {
        return taskStore.create(taskId);
    }

    /** Task'ı terminal state ile bitir. */
    public void finishTask(String taskId, TaskStore.State state, String reason) {
        taskStore.finish(taskId, state, reason);
    }

    /** Kullanıcı onayı verdiğinde grant üret. */
    public Grant issueGrant(String taskId, ToolCall call) {
        return grantStore.issue(taskId, call);
    }

    /**
     * ANA GİRİŞ NOKTASI.
     * Akış: INTENT → Policy → Permission → Grant → Router → RESULT
     */
    public com.atlas.app.runtime.ToolResult execute(String taskId, ToolCall call) {

        // 0. Pre-check
        if (taskId == null || taskId.trim().isEmpty()) {
            return com.atlas.app.runtime.ToolResult.failure(
                com.atlas.app.runtime.ToolResult.Source.LOCAL,
                "taskId required", -1);
        }
        if (call == null) {
            return com.atlas.app.runtime.ToolResult.failure(
                com.atlas.app.runtime.ToolResult.Source.LOCAL,
                "ToolCall required", -1);
        }
        if (!taskStore.isRunning(taskId)) {
            return com.atlas.app.runtime.ToolResult.failure(
                com.atlas.app.runtime.ToolResult.Source.LOCAL,
                "Task not running: " + taskId, -2);
        }

        // 1. WRITE-AHEAD INTENT (R4)
        ledger.writeIntent(taskId, call);

        // 2. Policy
        PolicyDecision policy = policyEngine.evaluate(
            new PolicyRequest(call.toolName(), call.action()));
        if (policy == PolicyDecision.DENY) {
            com.atlas.app.runtime.ToolResult r =
                com.atlas.app.runtime.ToolResult.denied(call);
            ledger.writeResult(taskId, call, r);
            return r;
        }

        // 3. Permission + Grant
        if (policy == PolicyDecision.REQUIRE_PERMISSION) {
            PermissionType pt = permissionResolver.resolve(call.toolName(), call.action());
            PermissionState ps = permissionChecker.check(pt);

            if (ps != PermissionState.AVAILABLE) {
                Grant consumed = grantStore.tryConsume(taskId, call);
                if (consumed == null) {
                    com.atlas.app.runtime.ToolResult r =
                        com.atlas.app.runtime.ToolResult.approvalRequired(
                            call,
                            "Permission: " + pt.name() + " (no valid grant)");
                    ledger.writeResult(taskId, call, r);
                    return r;
                }
                ledger.writeApproval(taskId, consumed.grantId());
            }
        }

        // 4. Tool var mı?
        AtlasTool legacyTool = toolRegistry.get(call.toolName());
        if (legacyTool == null) {
            com.atlas.app.runtime.ToolResult r =
                com.atlas.app.runtime.ToolResult.failure(
                    com.atlas.app.runtime.ToolResult.Source.LOCAL,
                    "Tool not found: " + call.toolName(), -3);
            ledger.writeResult(taskId, call, r);
            return r;
        }

        // 5. Route (yeni backend öncelik, fallback legacy Tool)
        com.atlas.app.runtime.ToolResult result;
        try {
            result = backendRouter.route(call);
            if (result == null) {
                // Backend yoksa legacy Tool API'sine düş
                // BULGU 5 DUZELTMESI: TreeMap alfabetik siraliyor. arg0, arg1... seklinde sirali al.
                java.util.List<String> orderedArgs = new java.util.ArrayList<>();
                if (call.args() != null) {
                    for (int i = 0; call.args().containsKey("arg" + i); i++) {
                        orderedArgs.add(call.args().get("arg" + i));
                    }
                    // Eger arg0 yoksa (eski tip kullanim), values() fallback
                    if (orderedArgs.isEmpty()) {
                        orderedArgs.addAll(call.args().values());
                    }
                }
                String[] argv = orderedArgs.toArray(new String[0]);

                com.atlas.app.tool.ToolResult legacyResult =
                    legacyTool.execute(call.action(), argv);
                result = convertLegacy(legacyResult);
            }
        } catch (Exception e) {
            result = com.atlas.app.runtime.ToolResult.failure(
                com.atlas.app.runtime.ToolResult.Source.LOCAL,
                "Execution error: " + e.getMessage(), -4);
        }

        // 6. WRITE RESULT (R4)
        ledger.writeResult(taskId, call, result);
        return result;
    }

    /** Eski ToolResult -> yeni ToolResult. */
    private com.atlas.app.runtime.ToolResult convertLegacy(
            com.atlas.app.tool.ToolResult legacy) {
        if (legacy == null) {
            return com.atlas.app.runtime.ToolResult.unknown(
                com.atlas.app.runtime.ToolResult.Source.LOCAL,
                "Legacy null");
        }
        if (legacy.success) {
            return com.atlas.app.runtime.ToolResult.success(
                com.atlas.app.runtime.ToolResult.Source.LOCAL,
                legacy.output, null);
        }
        return com.atlas.app.runtime.ToolResult.failure(
            com.atlas.app.runtime.ToolResult.Source.LOCAL,
            legacy.output, legacy.code);
    }

    // ==== Getter ====
    public EventLedger ledger() { return ledger; }
    public GrantStore grants() { return grantStore; }
    public TaskStore tasks() { return taskStore; }
    public CapabilityRegistry capabilities() { return capabilityRegistry; }
    public BackendRouter router() { return backendRouter; }
    public ToolRegistry toolRegistry() { return toolRegistry; }
}
