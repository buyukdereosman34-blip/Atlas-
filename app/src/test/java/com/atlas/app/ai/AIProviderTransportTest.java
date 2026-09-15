package com.atlas.app.ai;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AIProviderTransportTest {

    @Test
    public void requestStoresTransportData() {
        AITransportRequest request = new AITransportRequest(
                "https://example.com",
                "POST",
                Collections.singletonMap("Authorization", "Bearer test"),
                "{\"test\":true}"
        );

        assertEquals("https://example.com", request.getEndpoint());
        assertEquals("POST", request.getMethod());
        assertEquals("Bearer test",
                request.getHeaders().get("Authorization"));
        assertEquals("{\"test\":true}", request.getBody());
    }

    @Test
    public void invalidRequestFailsSafely() {
        HttpAIProviderTransport transport =
                new HttpAIProviderTransport();

        AITransportResponse response =
                transport.execute(
                        new AITransportRequest(
                                "",
                                "POST",
                                Collections.emptyMap(),
                                ""
                        )
                );

        assertFalse(response.isSuccess());
        assertEquals(-1, response.getStatusCode());
    }
}
