package com.atlas.app.ai;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AIProviderConfigLoaderTest {

    @Test
    public void usesDefaultEndpointAndModelWhenEnvironmentIsMissing() {
        AIProviderConfig config =
                AIProviderConfigLoader.load(
                        "openai",
                        "https://example.com",
                        "test-model"
                );

        assertEquals(
                "https://example.com",
                config.getEndpoint()
        );

        assertEquals(
                "test-model",
                config.getModel()
        );
    }

    @Test
    public void missingApiKeyIsAllowedAtConfigLayer() {
        AIProviderConfig config =
                AIProviderConfigLoader.load(
                        "ollama",
                        "http://127.0.0.1:11434/api/chat",
                        "llama3"
                );

        assertTrue(
                config.getApiKey().isEmpty()
        );
    }
}
