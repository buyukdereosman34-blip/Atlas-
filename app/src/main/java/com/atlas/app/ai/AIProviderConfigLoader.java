package com.atlas.app.ai;

public final class AIProviderConfigLoader {

    private AIProviderConfigLoader() {
    }

    public static AIProviderConfig load(
            String providerName,
            String defaultEndpoint,
            String defaultModel
    ) {
        String prefix =
                "ATLAS_AI_"
                        + normalizeProviderName(providerName)
                        + "_";

        String endpoint = read(prefix + "ENDPOINT");
        String model = read(prefix + "MODEL");
        String apiKey = read(prefix + "API_KEY");

        // Generic values remain as fallback.
        if (endpoint.isEmpty()) {
            endpoint = read("ATLAS_AI_ENDPOINT");
        }

        if (model.isEmpty()) {
            model = read("ATLAS_AI_MODEL");
        }

        if (apiKey.isEmpty()) {
            apiKey = read("ATLAS_AI_API_KEY");
        }

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

    private static String normalizeProviderName(String providerName) {
        if (providerName == null) {
            return "";
        }

        return providerName
                .trim()
                .toUpperCase()
                .replace('-', '_');
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
