package com.atlas.app.ai;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OpenAIProviderTest {

    @Test
    public void providerHasCloudType() {
        AIProviderConfig config =
                new AIProviderConfig(
                        "https://api.openai.com/v1/responses",
                        "test-model",
                        "test-key"
                );

        OpenAIProvider provider =
                new OpenAIProvider(
                        config,
                        request -> AITransportResponse.success(
                                200,
                                "{\"output_text\":\"hello\"}"
                        )
                );

        assertEquals("openai", provider.getName());
        assertEquals(
                AIProviderType.CLOUD,
                provider.getType()
        );
        assertFalse(provider.isAvailable() == false);
    }

    @Test
    public void providerParsesResponsesApiOutput() {
        AIProviderConfig config =
                new AIProviderConfig(
                        "https://api.openai.com/v1/responses",
                        "test-model",
                        "test-key"
                );

        OpenAIProvider provider =
                new OpenAIProvider(
                        config,
                        request -> AITransportResponse.success(
                                200,
                                "{\"output_text\":\"ATLAS works\"}"
                        )
                );

        AIResponse response =
                provider.generate(
                        new AIRequest(
                                "You are ATLAS.",
                                "Say hello."
                        )
                );

        assertEquals(true, response.isSuccess());
        assertEquals("ATLAS works", response.getOutput());
    }

    @Test
    public void providerFailsWithoutConfig() {
        AIProviderConfig config =
                new AIProviderConfig(
                        "",
                        "",
                        ""
                );

        OpenAIProvider provider =
                new OpenAIProvider(
                        config,
                        request -> AITransportResponse.success(
                                200,
                                "{\"output_text\":\"should not run\"}"
                        )
                );

        AIResponse response =
                provider.generate(
                        new AIRequest(
                                "",
                                "test"
                        )
                );

        assertFalse(response.isSuccess());
    }
}
