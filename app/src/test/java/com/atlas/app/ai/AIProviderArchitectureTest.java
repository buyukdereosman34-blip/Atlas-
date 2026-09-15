package com.atlas.app.ai;

import org.junit.Test;

import static org.junit.Assert.*;

public class AIProviderArchitectureTest {

    @Test
    public void cloudProviderUsesCloudType() {
        GenericCloudAIProvider provider =
                new GenericCloudAIProvider(
                        "openai",
                        new AIProviderConfig(
                                "https://example.com",
                                "model",
                                ""
                        )
                );

        assertEquals(
                AIProviderType.CLOUD,
                provider.getType()
        );

        assertTrue(
                provider.isAvailable()
        );
    }

    @Test
    public void localProviderUsesLocalType() {
        GenericLocalAIProvider provider =
                new GenericLocalAIProvider(
                        "ollama",
                        new AIProviderConfig(
                                "http://localhost",
                                "llama",
                                ""
                        )
                );

        assertEquals(
                AIProviderType.LOCAL,
                provider.getType()
        );

        assertTrue(
                provider.isAvailable()
        );
    }

    @Test
    public void hybridPrefersCloud() {
        AIProvider cloud =
                new GenericCloudAIProvider(
                        "cloud",
                        new AIProviderConfig(
                                "https://example.com",
                                "model",
                                ""
                        )
                );

        AIProvider local =
                new GenericLocalAIProvider(
                        "local",
                        new AIProviderConfig(
                                "http://localhost",
                                "model",
                                ""
                        )
                );

        GenericHybridAIProvider hybrid =
                new GenericHybridAIProvider(
                        "hybrid",
                        cloud,
                        local
                );

        assertTrue(
                hybrid.isAvailable()
        );

        AIResponse response =
                hybrid.generate(
                        new AIRequest(
                                "",
                                "test"
                        )
                );

        assertFalse(
                response.isSuccess()
        );

        assertTrue(
                response.getOutput()
                        .contains("Cloud AI")
        );
    }
}
