package com.atlas.app.ai;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AITransportRequest {

    private final String endpoint;
    private final String method;
    private final Map<String, String> headers;
    private final String body;

    public AITransportRequest(
            String endpoint,
            String method,
            Map<String, String> headers,
            String body
    ) {
        this.endpoint = endpoint;
        this.method = method == null ? "POST" : method;
        this.headers = headers == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(headers));
        this.body = body;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
