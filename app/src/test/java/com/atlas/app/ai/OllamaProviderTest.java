package com.atlas.app.ai;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OllamaProviderTest {

    @Test
    public void providerHasLocalType() {
        AIProviderConfig config =
                new AIProviderConfig(
                        "http://127.0.0.1:11434/api/chat",
                        "llama3.2",
                        ""
                );

        OllamaProvider provider =
                new OllamaProvider(
                        config,
                        request -> AITransportResponse.success(
                                200,
                                "{\"message\":{\"role\":\"assistant\",\"content\":\"hello\"}}"
                        )
                );

        assertEquals("ollama", provider.getName());
        assertEquals(
                AIProviderType.LOCAL,
                provider.getType()
        );
        assertTrue(provider.isAvailable());
    }

    @Test
    public void providerParsesChatOutput() {
        AIProviderConfig config =
                new AIProviderConfig(
                        "http://127.0.0.1:11434/api/chat",
                        "llama3.2",
                        ""
                );

        OllamaProvider provider =
                new OllamaProvider(
                        config,
                        request -> AITransportResponse.success(
                                200,
                                "{\"message\":{\"role\":\"assistant\",\"content\":\"ATLAS works\"}}"
                        )
                );

        AIResponse response =
                provider.generate(
                        new AIRequest(
                                "You are ATLAS.",
                                "Say hello."
                        )
                );

        assertTrue(response.isSuccess());
        assertEquals(
                "ATLAS works",
                response.getOutput()
        );
    }

    @Test
    public void providerBuildsOllamaRequest() {
        AIProviderConfig config =
                new AIProviderConfig(
                        "http://127.0.0.1:11434/api/chat",
                        "llama3.2",
                        ""
                );

        final AITransportRequest[] captured =
                new AITransportRequest[1];

        OllamaProvider provider =
                new OllamaProvider(
                        config,
                        request -> {
                            captured[0] = request;

                            return AITransportResponse.success(
                                    200,
                                    "{\"message\":{\"role\":\"assistant\",\"content\":\"ok\"}}"
                            );
                        }
                );

        AIResponse response =
                provider.generate(
                        new AIRequest(
                                "System prompt",
                                "User prompt"
                        )
                );

        assertTrue(response.isSuccess());
        assertEquals(
                "POST",
                captured[0].getMethod()
        );
        assertEquals(
                "application/json",
                captured[0]
                        .getHeaders()
                        .get("Content-Type")
        );
        assertTrue(
                captured[0]
                        .getBody()
                        .contains("\"model\":\"llama3.2\"")
        );
        assertTrue(
                captured[0]
                        .getBody()
                        .contains("\"stream\":false")
        );
    }

    @Test
    public void providerFailsWithoutConfig() {
        AIProviderConfig config =
                new AIProviderConfig("", "", "");

        OllamaProvider provider =
                new OllamaProvider(
                        config,
                        request -> AITransportResponse.success(
                                200,
                                "{\"message\":{\"content\":\"should not run\"}}"
                        )
                );

        AIResponse response =
                provider.generate(
                        new AIRequest("", "test")
                );

        assertFalse(response.isSuccess());
    }
}
