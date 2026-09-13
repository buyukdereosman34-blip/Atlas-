package com.atlas.app.policy;

public class PolicyRequest {

    private final String toolName;
    private final String action;
    private final String[] args;

    public PolicyRequest(String toolName, String action, String... args) {
        this.toolName = toolName;
        this.action = action;
        this.args = args != null ? args.clone() : new String[0];
    }

    public String getToolName() {
        return toolName;
    }

    public String getAction() {
        return action;
    }

    public String[] getArgs() {
        return args.clone();
    }
}
