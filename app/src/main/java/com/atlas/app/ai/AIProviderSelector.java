package com.atlas.app.ai;

public class AIProviderSelector {

    private final AIProviderManager manager;

    public AIProviderSelector(
            AIProviderManager manager
    ) {
        this.manager = manager;
    }

    public ProviderSelection select() {
        if (manager == null) {
            return ProviderSelection.unavailable(
                    "AI provider manager is missing"
            );
        }

        AIProvider provider =
                manager.getProvider();

        if (provider == null) {
            return ProviderSelection.unavailable(
                    "No available AI provider"
            );
        }

        return ProviderSelection.selected(
                provider
        );
    }

    public ProviderSelection select(
            AIProviderType type
    ) {
        if (manager == null) {
            return ProviderSelection.unavailable(
                    "AI provider manager is missing"
            );
        }

        AIProvider provider =
                manager.getProvider(type);

        if (provider == null) {
            return ProviderSelection.unavailable(
                    "No available "
                            + type
                            + " AI provider"
            );
        }

        return ProviderSelection.selected(
                provider
        );
    }
}
