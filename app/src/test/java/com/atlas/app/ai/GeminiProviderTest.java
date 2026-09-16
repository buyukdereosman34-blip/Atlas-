package com.atlas.app.ai;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GeminiProviderTest {

    @Test
    public void providerHasCloudType() {
        AIProviderConfig config =
                new AIProviderConfig(
                        "https://generativelanguage.googleapis.com/v1beta/models/gemini-test:generateContent",
                        "gemini-test",
                        "test-key"
                );

        GeminiProvider provider =
                new GeminiProvider(
                        config,
                        request -> AITransportResponse.success(
                                200,
                                "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"hello\"}]}}]}"
                        )
                );

        assertEquals(
                "gemini",
                provider.getName()
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
    public void providerParsesGenerateContentOutput() {
        AIProviderConfig config =
                new AIProviderConfig(
                        "https://generativelanguage.googleapis.com/v1beta/models/gemini-test:generateContent",
                        "gemini-test",
                        "test-key"
                );

        GeminiProvider provider =
                new GeminiProvider(
                        config,
                        request -> AITransportResponse.success(
                                200,
                                "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"ATLAS works\"}]}}]}"
                        )
                );

        AIResponse response =
                provider.generate(
                        new AIRequest(
                                "You are ATLAS.",
                                "Say hello."
                        )
                );

        assertTrue(
                response.isSuccess()
        );

        assertEquals(
                "ATLAS works",
                response.getOutput()
        );
    }

    @Test
    public void providerFailsWithoutConfig() {
        AIProviderConfig config =
                new AIProviderConfig(
                        "",
                        "",
                        ""
                );

        GeminiProvider provider =
                new GeminiProvider(
                        config,
                        request -> AITransportResponse.success(
                                200,
                                "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"should not run\"}]}}]}"
                        )
                );

        AIResponse response =
                provider.generate(
                        new AIRequest(
                                "",
                                "test"
                        )
                );

        assertFalse(
                response.isSuccess()
        );
    }
}
