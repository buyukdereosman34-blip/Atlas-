package com.atlas.app.ai;

public class GenericCloudAIProvider
        extends CloudAIProviderBase {

    private final String name;

    public GenericCloudAIProvider(
            String name,
            AIProviderConfig config
    ) {
        super(config);
        this.name =
                name == null
                        ? "cloud"
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
