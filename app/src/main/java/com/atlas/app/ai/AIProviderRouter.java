package com.atlas.app.ai;

import java.util.ArrayList;
import java.util.List;

public class AIProviderRouter {

    private final AIProviderRegistry registry;

    public AIProviderRouter(AIProviderRegistry registry) {
        this.registry = registry;
    }

    public ProviderSelection route(AITaskType taskType) {
        return route(taskType, false);
    }

    public ProviderSelection route(
            AITaskType taskType,
            boolean localOnly
    ) {
        if (registry == null) {
            return ProviderSelection.unavailable(
                    "AI provider registry is missing"
            );
        }

        List<AIProvider> available =
                new ArrayList<>();

        for (AIProvider provider : registry.getAll()) {
            if (provider != null && provider.isAvailable()) {
                available.add(provider);
            }
        }

        if (available.isEmpty()) {
            return ProviderSelection.unavailable(
                    "No available AI provider"
            );
        }

        if (localOnly
                || taskType == AITaskType.PRIVATE
                || taskType == AITaskType.LOCAL_AUTOMATION) {

            AIProvider local = findType(
                    available,
                    AIProviderType.LOCAL
            );

            if (local != null) {
                return ProviderSelection.selected(local);
            }

            if (localOnly) {
                return ProviderSelection.unavailable(
                        "No available local AI provider"
                );
            }
        }

        AIProvider preferred = findPreferred(
                available,
                taskType
        );

        if (preferred != null) {
            return ProviderSelection.selected(preferred);
        }

        AIProvider hybrid = findType(
                available,
                AIProviderType.HYBRID
        );

        if (hybrid != null) {
            return ProviderSelection.selected(hybrid);
        }

        AIProvider cloud = findType(
                available,
                AIProviderType.CLOUD
        );

        if (cloud != null) {
            return ProviderSelection.selected(cloud);
        }

        return ProviderSelection.selected(
                available.get(0)
        );
    }

    private AIProvider findPreferred(
            List<AIProvider> providers,
            AITaskType taskType
    ) {
        if (taskType == null) {
            return null;
        }

        switch (taskType) {

            case CODING:
            case CODE_REVIEW:
            case ANALYSIS:
            case PLANNING:
                AIProvider cloud =
                        findType(
                                providers,
                                AIProviderType.CLOUD
                        );

                if (cloud != null) {
                    return cloud;
                }

                return findType(
                        providers,
                        AIProviderType.LOCAL
                );

            case CREATIVE:
            case GENERAL:
                return findType(
                        providers,
                        AIProviderType.CLOUD
                );

            default:
                return null;
        }
    }

    private AIProvider findType(
            List<AIProvider> providers,
            AIProviderType type
    ) {
        for (AIProvider provider : providers) {
            if (provider.getType() == type) {
                return provider;
            }
        }

        return null;
    }
}
