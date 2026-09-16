package com.atlas.app.ai;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClaudeProviderTest {

    @Test
    public void providerHasCloudType() {
        AIProviderConfig config =
                new AIProviderConfig(
                        "https://api.anthropic.com/v1/messages",
                        "claude-test",
                        "test-key"
                );

        ClaudeProvider provider =
                new ClaudeProvider(
                        config,
                        request -> AITransportResponse.success(
                                200,
                                "{\"content\":[{\"type\":\"text\",\"text\":\"hello\"}]}"
                        )
                );

        assertEquals("claude", provider.getName());
        assertEquals(
                AIProviderType.CLOUD,
                provider.getType()
        );
        assertTrue(provider.isAvailable());
    }

    @Test
    public void providerParsesMessagesOutput() {
        AIProviderConfig config =
                new AIProviderConfig(
                        "https://api.anthropic.com/v1/messages",
                        "claude-test",
                        "test-key"
                );

        ClaudeProvider provider =
                new ClaudeProvider(
                        config,
                        request -> AITransportResponse.success(
                                200,
                                "{\"content\":[{\"type\":\"text\",\"text\":\"ATLAS works\"}]}"
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
    public void providerBuildsAnthropicRequest() {
        AIProviderConfig config =
                new AIProviderConfig(
                        "https://api.anthropic.com/v1/messages",
                        "claude-test",
                        "test-key"
                );

        final AITransportRequest[] captured =
                new AITransportRequest[1];

        ClaudeProvider provider =
                new ClaudeProvider(
                        config,
                        request -> {
                            captured[0] = request;

                            return AITransportResponse.success(
                                    200,
                                    "{\"content\":[{\"type\":\"text\",\"text\":\"ok\"}]}"
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
                "test-key",
                captured[0]
                        .getHeaders()
                        .get("x-api-key")
        );
        assertEquals(
                "2023-06-01",
                captured[0]
                        .getHeaders()
                        .get("anthropic-version")
        );
        assertTrue(
                captured[0]
                        .getBody()
                        .contains("\"model\":\"claude-test\"")
        );
    }

    @Test
    public void providerFailsWithoutConfig() {
        AIProviderConfig config =
                new AIProviderConfig("", "", "");

        ClaudeProvider provider =
                new ClaudeProvider(
                        config,
                        request -> AITransportResponse.success(
                                200,
                                "{\"content\":[{\"type\":\"text\",\"text\":\"should not run\"}]}"
                        )
                );

        AIResponse response =
                provider.generate(
                        new AIRequest("", "test")
                );

        assertFalse(response.isSuccess());
    }
}
