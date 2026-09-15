package com.atlas.app.ai;

public abstract class HybridAIProvider
        implements AIProvider {

    @Override
    public final AIProviderType getType() {
        return AIProviderType.HYBRID;
    }
}
