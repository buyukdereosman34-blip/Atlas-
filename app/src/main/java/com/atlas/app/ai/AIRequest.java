package com.atlas.app.ai;

public class AIRequest {

    private final String systemPrompt;
    private final String userPrompt;

    public AIRequest(
            String systemPrompt,
            String userPrompt
    ) {
        this.systemPrompt = systemPrompt == null
                ? ""
                : systemPrompt;
        this.userPrompt = userPrompt == null
                ? ""
                : userPrompt;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public String getUserPrompt() {
        return userPrompt;
    }
}
