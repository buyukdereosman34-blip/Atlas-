package com.atlas.app.runtime;

import com.atlas.app.ai.AIProvider;
import com.atlas.app.ai.AIProviderRouter;
import com.atlas.app.ai.AIRequest;
import com.atlas.app.ai.AIResponse;
import com.atlas.app.ai.AITaskType;
import com.atlas.app.ai.LLMOutputGuard;

/**
 * Yeni nesil ajan döngüsü.
 *
 * Akış:
 *   User prompt → LLM → LLMOutputGuard → ToolCall → AtlasExecutor
 *                → APPROVAL_REQUIRED ise ApprovalManager
 *                → SUCCESS ise task DONE
 *                → FAILURE ise retry veya FAILED
 *
 * Kurallar:
 *   - maxSteps: 10 (sonsuz döngü yok)
 *   - maxRetries: 3 (aynı hata için)
 *   - Stuck detection: aynı tool/action 3 kez fail ederse ABORTED
 *   - LLM cevabı "tool yok" ise DONE (konuşma bitti)
 */
public final class AgentLoop {

    public interface Listener {
        void onProgress(String taskId, int step, String message);
        void onToolCall(String taskId, ToolCall call, ToolResult result);
        void onComplete(LoopResult result);
    }

    public static final class LoopResult {
        public final String taskId;
        public final TaskStore.State finalState;
        public final String finalOutput;
        public final int stepsUsed;
        public final int retriesUsed;

        public LoopResult(String taskId, TaskStore.State state,
                          String output, int steps, int retries) {
            this.taskId = taskId;
            this.finalState = state;
            this.finalOutput = output == null ? "" : output;
            this.stepsUsed = steps;
            this.retriesUsed = retries;
        }
    }

    private final AtlasExecutor executor;
    private final AIProviderRouter router;
    private final LLMOutputGuard guard;
    private final ApprovalManager approval;

    private final int maxSteps;
    private final int maxRetries;

    public AgentLoop(AtlasExecutor executor,
                     AIProviderRouter router,
                     ApprovalManager approval) {
        this(executor, router, approval, 10, 3);
    }

    public AgentLoop(AtlasExecutor executor,
                     AIProviderRouter router,
                     ApprovalManager approval,
                     int maxSteps,
                     int maxRetries) {
        this.executor = executor;
        this.router = router;
        this.guard = new LLMOutputGuard(executor.toolRegistry());
        this.approval = approval;
        this.maxSteps = maxSteps > 0 ? maxSteps : 10;
        this.maxRetries = maxRetries > 0 ? maxRetries : 3;
    }

    /**
     * Ana döngü.
     * @param taskId Unique task ID
     * @param userPrompt Kullanıcının doğal dil isteği
     * @param listener Opsiyonel UI geri çağırma
     */
    public LoopResult run(String taskId, String userPrompt, Listener listener) {
        if (taskId == null || taskId.trim().isEmpty()) {
            return new LoopResult("", TaskStore.State.FAILED,
                    "taskId required", 0, 0);
        }

        executor.startTask(taskId);

        StringBuilder conversation = new StringBuilder();
        if (userPrompt != null) {
            conversation.append(userPrompt);
        }

        String lastSignature = "";
        int consecutiveSameFailures = 0;

        int steps = 0;
        int retries = 0;

        try {
            for (int step = 1; step <= maxSteps; step++) {
                steps = step;

                if (listener != null) {
                    listener.onProgress(taskId, step, "LLM cagriliyor...");
                }

                AIProvider provider = selectProvider();
                if (provider == null) {
                    return finish(taskId, TaskStore.State.FAILED,
                            "No AI provider available", steps, retries, listener);
                }

                AIResponse ai = provider.generate(
                        new AIRequest(systemPrompt(), conversation.toString())
                );

                if (ai == null || !ai.isSuccess()) {
                    return finish(taskId, TaskStore.State.FAILED,
                            "AI error: "
                                    + (ai == null ? "null" : ai.getOutput()),
                            steps, retries, listener);
                }

                LLMOutputGuard.Guarded g = guard.sanitize(ai.getOutput());

                if (!g.valid) {
                    // LLM tool değil, serbest metin döndürdü → DONE
                    return finish(taskId, TaskStore.State.DONE,
                            ai.getOutput(), steps, retries, listener);
                }

                ToolCall call = g.call;

                if (listener != null) {
                    listener.onProgress(taskId, step,
                            "Tool: " + call.toolName() + "/" + call.action());
                }

                ToolResult result = executor.execute(taskId, call);

                // APPROVAL_REQUIRED ise onay al
                if (result != null
                        && result.status == ToolResult.Status.APPROVAL_REQUIRED) {

                    if (listener != null) {
                        listener.onProgress(taskId, step,
                                "Kullanici onayi bekleniyor...");
                    }

                    boolean approved = approval.requestApproval(taskId, call);

                    if (!approved) {
                        return finish(taskId, TaskStore.State.ABORTED,
                                "User rejected: "
                                        + call.toolName() + "/" + call.action(),
                                steps, retries, listener);
                    }

                    // Grant üret ve tekrar dene
                    executor.issueGrant(taskId, call);
                    result = executor.execute(taskId, call);
                }

                if (listener != null) {
                    listener.onToolCall(taskId, call, result);
                }

                String signature = call.toolName() + "/" + call.action();

                if (result != null && result.isSuccess()) {
                    conversation.append("\n[step ").append(step).append("] ")
                            .append(call.toolName()).append("/").append(call.action())
                            .append(" -> ").append(result.output);
                    consecutiveSameFailures = 0;
                    continue;
                }

                // FAILURE
                String output = result == null
                        ? "null result"
                        : result.output;

                if (signature.equals(lastSignature)) {
                    consecutiveSameFailures++;
                } else {
                    lastSignature = signature;
                    consecutiveSameFailures = 1;
                }

                if (consecutiveSameFailures >= 3) {
                    return finish(taskId, TaskStore.State.FAILED,
                            "Stuck: same call failed "
                                    + consecutiveSameFailures + "x: " + signature,
                            steps, retries, listener);
                }

                if (retries < maxRetries) {
                    retries++;
                    conversation.append("\n[step ").append(step).append("] ")
                            .append("RETRY ").append(retries)
                            .append(": ").append(output);
                    continue;
                }

                return finish(taskId, TaskStore.State.FAILED,
                        "Max retries exhausted: " + output,
                        steps, retries, listener);
            }

            return finish(taskId, TaskStore.State.BUDGET_EXHAUSTED,
                    "Max steps reached: " + maxSteps,
                    steps, retries, listener);

        } catch (Exception e) {
            return finish(taskId, TaskStore.State.FAILED,
                    "Exception: " + e.getClass().getSimpleName()
                            + ": " + e.getMessage(),
                    steps, retries, listener);
        }
    }

    private AIProvider selectProvider() {
        if (router == null) return null;
        try {
            com.atlas.app.ai.ProviderSelection sel =
                    router.route(AITaskType.GENERAL);
            return sel != null && sel.isSuccess() ? sel.getProvider() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private LoopResult finish(String taskId,
                              TaskStore.State state,
                              String output,
                              int steps,
                              int retries,
                              Listener listener) {
        try {
            executor.finishTask(taskId, state, output);
        } catch (Exception ignored) {
        }

        LoopResult result = new LoopResult(taskId, state, output, steps, retries);

        if (listener != null) {
            try {
                listener.onComplete(result);
            } catch (Exception ignored) {
            }
        }

        return result;
    }

    private String systemPrompt() {
        return "Sen ATLAS'sin, Android cihazlarda calisan bir yapay zeka ajanisin. "
                + "Cevaplarini HER ZAMAN Turkce ver. "
                + "SADECE su an elinde olan araclari kullanabilirsin: "
                + "control (status), apps (status, list), screen (status, info). "
                + "Bunlarin disinda bir arac veya ozellik istendiginde "
                + "'Bu ozelligim henuz yok' de. "
                + "ASLA olmayan bir araci veya ozelligi uydurma. "
                + "Bir arac kullanman gerekiyorsa SADECE su JSON formatinda cevap ver: "
                + "{\"tool\":\"control\",\"action\":\"status\",\"args\":{}}. "
                + "Arac gerekmiyorsa (sohbet), duz Turkce metin olarak cevap ver (JSON YOK).";
    }
}
