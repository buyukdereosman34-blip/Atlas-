package com.atlas.app.ai;

public final class AIProviderConfigLoader {

    private AIProviderConfigLoader() {
    }

    public static AIProviderConfig load(
            String providerName,
            String defaultEndpoint,
            String defaultModel
    ) {
        String endpoint = read("ATLAS_AI_ENDPOINT");
        String model = read("ATLAS_AI_MODEL");
        String apiKey = read("ATLAS_AI_API_KEY");

        if (endpoint.isEmpty()) {
            endpoint = defaultEndpoint == null
                    ? ""
                    : defaultEndpoint;
        }

        if (model.isEmpty()) {
            model = defaultModel == null
                    ? ""
                    : defaultModel;
        }

        return new AIProviderConfig(
                endpoint,
                model,
                apiKey
        );
    }

    public static String getProviderName() {
        return read("ATLAS_AI_PROVIDER");
    }

    private static String read(String name) {
        String value = System.getenv(name);

        if (value == null || value.trim().isEmpty()) {
            value = System.getProperty(name);
        }

        return value == null
                ? ""
                : value.trim();
    }
}
