package com.atlas.app.ai;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeminiProviderTest {

    @Test
    public void generateBuildsGeminiRequestCorrectly() {

        AIProviderConfig config =
                new AIProviderConfig(
                        GeminiProvider.DEFAULT_ENDPOINT,
                        "gemini-3.6-flash",
                        "test-api-key"
                );

        CapturingTransport transport =
                new CapturingTransport();

        GeminiProvider provider =
                new GeminiProvider(
                        config,
                        transport
                );

        AIResponse response =
                provider.generate(
                        new AIRequest(
                                "You are ATLAS.",
                                "Write OK."
                        )
                );

        assertTrue(response.isSuccess());

        assertEquals(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent",
                transport.request.getEndpoint()
        );

        assertEquals(
                "test-api-key",
                transport.request.getHeaders()
                        .get("x-goog-api-key")
        );

        assertTrue(
                transport.request.getBody()
                        .contains("\"contents\"")
        );

        assertTrue(
                transport.request.getBody()
                        .contains("Write OK.")
        );
    }

    private static class CapturingTransport
            implements AIProviderTransport {

        private AITransportRequest request;

        @Override
        public AITransportResponse execute(
                AITransportRequest request
        ) {
            this.request = request;

            return AITransportResponse.success(
                    200,
                    "{"
                            + "\"candidates\":[{"
                            + "\"content\":{"
                            + "\"parts\":[{"
                            + "\"text\":\"OK\"}"
                            + "]}"
                            + "}]"
                            + "}"
            );
        }
    }
}
