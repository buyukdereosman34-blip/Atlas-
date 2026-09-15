package com.atlas.app.ai;

public class AIProviderConfig {

    private final String endpoint;
    private final String model;
    private final String apiKey;

    public AIProviderConfig(
            String endpoint,
            String model,
            String apiKey
    ) {
        this.endpoint = endpoint == null ? "" : endpoint;
        this.model = model == null ? "" : model;
        this.apiKey = apiKey == null ? "" : apiKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getModel() {
        return model;
    }

    public String getApiKey() {
        return apiKey;
    }

    public boolean isConfigured() {
        return !endpoint.trim().isEmpty()
                && !model.trim().isEmpty();
    }
}
