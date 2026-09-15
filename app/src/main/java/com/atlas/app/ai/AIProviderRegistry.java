package com.atlas.app.ai;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class AIProviderRegistry {

    private final Map<String, AIProvider> providers =
            new LinkedHashMap<>();

    public void register(AIProvider provider) {
        if (provider == null) {
            return;
        }

        String name = provider.getName();

        if (name == null || name.trim().isEmpty()) {
            return;
        }

        providers.put(
                name.trim().toLowerCase(),
                provider
        );
    }

    public AIProvider get(String name) {
        if (name == null) {
            return null;
        }

        return providers.get(
                name.trim().toLowerCase()
        );
    }

    public AIProvider getAvailable(
            AIProviderType type
    ) {
        for (AIProvider provider : providers.values()) {
            if (!provider.isAvailable()) {
                continue;
            }

            if (type == null
                    || provider.getType() == type) {
                return provider;
            }
        }

        return null;
    }

    public Collection<AIProvider> getAll() {
        return providers.values();
    }

    public boolean contains(String name) {
        return get(name) != null;
    }

    public int size() {
        return providers.size();
    }
}
