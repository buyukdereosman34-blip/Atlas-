package com.atlas.app.ai;

public class UnavailableAIProvider
        implements AIProvider {

    private final String name;
    private final AIProviderType type;

    public UnavailableAIProvider(
            String name,
            AIProviderType type
    ) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AIProviderType getType() {
        return type;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public AIResponse generate(
            AIRequest request
    ) {
        return AIResponse.failure(
                "AI provider is not available: "
                        + name,
                -1
        );
    }
}
