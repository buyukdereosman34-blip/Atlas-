package com.atlas.app.ai;

public abstract class CloudAIProviderBase
        extends CloudAIProvider {

    protected final AIProviderConfig config;

    protected final AIProviderConfig getConfig() {
        return config;
    }


    protected CloudAIProviderBase(
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
                "Cloud AI provider is not configured: "
                        + getName(),
                -20
        );
    }
}
