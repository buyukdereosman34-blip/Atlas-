package com.atlas.app.ai;

public abstract class LocalAIProviderBase
        extends LocalAIProvider {

    protected final AIProviderConfig config;

    protected LocalAIProviderBase(
            AIProviderConfig config
    ) {
        this.config = config;
    }

    @Override
    public boolean isAvailable() {
        return config != null
                && config.isConfigured();
    }

    protected AIResponse unavailable() {
        return AIResponse.failure(
                "Local AI provider is not configured: "
                        + getName(),
                -21
        );
    }
}
