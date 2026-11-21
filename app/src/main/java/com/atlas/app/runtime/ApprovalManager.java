package com.atlas.app.runtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Kullanıcı onayı köprüsü.
 *
 * AtlasExecutor APPROVAL_REQUIRED döndürdüğünde, bu sınıf:
 *   - UI listener'a çağrı gönderir (varsa)
 *   - listener yoksa: REDDET (fail-safe)
 *   - timeout: 5 dakika, sonrası REDDET
 */
public final class ApprovalManager {

    public interface Listener {
        /**
         * UI'da onay dialog'u aç.
         * @param taskId Task ID
         * @param call ToolCall (onay isteyen çağrı)
         * @param response Callback: true = onayla, false = reddet
         */
        void onApprovalRequired(String taskId, ToolCall call, ApprovalResponse response);
    }

    public interface ApprovalResponse {
        void respond(boolean approved);
    }

    private static final long TIMEOUT_MS = 5 * 60 * 1000L; // 5 dakika

    private final Map<String, PendingApproval> pending = new ConcurrentHashMap<>();
    private volatile Listener listener;

    public void setListener(Listener l) {
        this.listener = l;
    }

    /**
     * Senkron onay iste. Blocks until response or timeout.
     * Called by AgentLoop after AtlasExecutor returns APPROVAL_REQUIRED.
     */
    public boolean requestApproval(String taskId, ToolCall call) {
        Listener l = this.listener;

        if (l == null) {
            // UI yoksa fail-safe: reddet
            System.err.println("[ApprovalManager] Listener yok, reddediliyor: "
                    + taskId + " / " + call.toolName() + "/" + call.action());
            return false;
        }

        String key = taskId + "#" + call.canonicalHash();
        PendingApproval pendingApproval = new PendingApproval();
        pending.put(key, pendingApproval);

        try {
            l.onApprovalRequired(taskId, call, pendingApproval);

            // Timeout ile bekle
            synchronized (pendingApproval) {
                long deadline = System.currentTimeMillis() + TIMEOUT_MS;
                while (!pendingApproval.done) {
                    long remaining = deadline - System.currentTimeMillis();
                    if (remaining <= 0) {
                        return false;
                    }
                    try {
                        pendingApproval.wait(remaining);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
                return pendingApproval.approved;
            }
        } finally {
            pending.remove(key);
        }
    }

    private static final class PendingApproval implements ApprovalResponse {
        volatile boolean done = false;
        volatile boolean approved = false;

        @Override
        public void respond(boolean a) {
            synchronized (this) {
                this.approved = a;
                this.done = true;
                this.notifyAll();
            }
        }
    }
}
