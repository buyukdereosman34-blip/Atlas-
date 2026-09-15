package com.atlas.app.ai;

public class GenericHybridAIProvider
        extends HybridAIProviderBase {

    private final String name;

    public GenericHybridAIProvider(
            String name,
            AIProvider cloudProvider,
            AIProvider localProvider
    ) {
        super(
                cloudProvider,
                localProvider
        );

        this.name =
                name == null
                        ? "hybrid"
                        : name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AIResponse generate(
            AIRequest request
    ) {
        AIProvider provider =
                selectProvider();

        if (provider == null) {
            return AIResponse.failure(
                    "No available cloud or local provider",
                    -22
            );
        }

        return provider.generate(request);
    }
}
