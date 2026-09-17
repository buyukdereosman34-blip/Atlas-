package com.atlas.app.improvement;

import com.atlas.app.ai.AIProviderBootstrap;
import com.atlas.app.ai.AIProviderRouter;

public final class AtlasDevelopmentEngine {

    private final AIProviderRouter aiProviderRouter;
    private final SelfImprovementEngine selfImprovementEngine;

    public AtlasDevelopmentEngine() {
        this(
                new ImprovementMethodStore()
        );
    }

    public AtlasDevelopmentEngine(
            ImprovementMethodStore methodStore
    ) {
        this.aiProviderRouter =
                AIProviderBootstrap.createRouter();

        this.selfImprovementEngine =
                new SelfImprovementEngine(
                        methodStore,
                        aiProviderRouter
                );
    }

    public AIProviderRouter getAIProviderRouter() {
        return aiProviderRouter;
    }

    public SelfImprovementEngine getSelfImprovementEngine() {
        return selfImprovementEngine;
    }
}
