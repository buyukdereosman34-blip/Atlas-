package com.atlas.app.ai;

public abstract class CloudAIProvider
        implements AIProvider {

    @Override
    public final AIProviderType getType() {
        return AIProviderType.CLOUD;
    }
}
