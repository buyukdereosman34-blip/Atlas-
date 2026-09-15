package com.atlas.app.ai;

public class AIProviderManager {

    private final AIProviderRegistry registry;
    private AIProviderType preferredType;

    public AIProviderManager() {
        this(new AIProviderRegistry());
    }

    public AIProviderManager(
            AIProviderRegistry registry
    ) {
        this.registry =
                registry == null
                        ? new AIProviderRegistry()
                        : registry;
    }

    public void setPreferredType(
            AIProviderType type
    ) {
        this.preferredType = type;
    }

    public AIProviderType getPreferredType() {
        return preferredType;
    }

    public AIProvider getProvider() {
        AIProvider provider =
                registry.getAvailable(
                        preferredType
                );

        if (provider != null) {
            return provider;
        }

        return registry.getAvailable(null);
    }

    public AIProvider getProvider(
            AIProviderType type
    ) {
        return registry.getAvailable(type);
    }

    public AIProviderRegistry getRegistry() {
        return registry;
    }

    public void register(
            AIProvider provider
    ) {
        registry.register(provider);
    }
}
