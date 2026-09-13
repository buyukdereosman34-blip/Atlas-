package com.atlas.app.policy;

public class PolicyEngine {

    public PolicyDecision evaluate(PolicyRequest request) {
        if (request == null) {
            return PolicyDecision.DENY;
        }

        String toolName = request.getToolName();
        String action = request.getAction();

        if (toolName == null || toolName.trim().isEmpty()
                || action == null || action.trim().isEmpty()) {
            return PolicyDecision.DENY;
        }

        String normalizedTool = toolName.trim();
        String normalizedAction = action.trim();

        if ("control".equalsIgnoreCase(normalizedTool)
                && "status".equalsIgnoreCase(normalizedAction)) {
            return PolicyDecision.ALLOW;
        }

        if ("apps".equalsIgnoreCase(normalizedTool)
                && ("status".equalsIgnoreCase(normalizedAction)
                || "list".equalsIgnoreCase(normalizedAction))) {
            return PolicyDecision.ALLOW;
        }

        if ("screen".equalsIgnoreCase(normalizedTool)
                && ("status".equalsIgnoreCase(normalizedAction)
                || "info".equalsIgnoreCase(normalizedAction))) {
            return PolicyDecision.ALLOW;
        }

        return PolicyDecision.REQUIRE_PERMISSION;
    }
}
