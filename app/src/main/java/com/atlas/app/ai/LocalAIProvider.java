package com.atlas.app.ai;

public abstract class LocalAIProvider
        implements AIProvider {

    @Override
    public final AIProviderType getType() {
        return AIProviderType.LOCAL;
    }
}
