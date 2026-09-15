package com.atlas.app.ai;

public class GenericLocalAIProvider
        extends LocalAIProviderBase {

    private final String name;

    public GenericLocalAIProvider(
            String name,
            AIProviderConfig config
    ) {
        super(config);
        this.name =
                name == null
                        ? "local"
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
        return unavailable();
    }
}
