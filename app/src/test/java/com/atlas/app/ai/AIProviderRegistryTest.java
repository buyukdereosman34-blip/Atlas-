package com.atlas.app.ai;

import org.junit.Test;

import static org.junit.Assert.*;

public class AIProviderRegistryTest {

    @Test
    public void registerAndGetProvider() {
        AIProviderRegistry registry =
                new AIProviderRegistry();

        AIProvider provider =
                new UnavailableAIProvider(
                        "openai",
                        AIProviderType.CLOUD
                );

        registry.register(provider);

        assertEquals(
                1,
                registry.size()
        );

        assertSame(
                provider,
                registry.get("OPENAI")
        );
    }

    @Test
    public void selectAvailableProviderByType() {
        AIProviderRegistry registry =
                new AIProviderRegistry();

        registry.register(
                new UnavailableAIProvider(
                        "openai",
                        AIProviderType.CLOUD
                )
        );

        assertNull(
                registry.getAvailable(
                        AIProviderType.CLOUD
                )
        );
    }

    @Test
    public void requestAndResponseWork() {
        AIRequest request =
                new AIRequest(
                        "system",
                        "generate code"
                );

        assertEquals(
                "system",
                request.getSystemPrompt()
        );

        assertEquals(
                "generate code",
                request.getUserPrompt()
        );

        AIResponse response =
                AIResponse.success("result");

        assertTrue(
                response.isSuccess()
        );

        assertEquals(
                "result",
                response.getOutput()
        );
    }
}
