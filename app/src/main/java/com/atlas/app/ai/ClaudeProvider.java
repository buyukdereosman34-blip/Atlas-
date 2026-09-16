package com.atlas.app.ai;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ClaudeProvider extends CloudAIProviderBase {

    public static final String DEFAULT_ENDPOINT =
            "https://api.anthropic.com/v1/messages";

    private final AIProviderTransport transport;

    public ClaudeProvider(
            AIProviderConfig config,
            AIProviderTransport transport
    ) {
        super(config);
        this.transport = transport;
    }

    @Override
    public String getName() {
        return "claude";
    }

    @Override
    public AIResponse generate(AIRequest request) {
        if (!isAvailable()) {
            return unavailable();
        }

        if (request == null) {
            return AIResponse.failure(
                    "Claude request is null",
                    -50
            );
        }

        if (transport == null) {
            return AIResponse.failure(
                    "Claude transport is unavailable",
                    -51
            );
        }

        try {
            JSONObject body = new JSONObject();

            body.put("model", getConfig().getModel());
            body.put("max_tokens", 4096);

            String systemPrompt = request.getSystemPrompt();
            String userPrompt = request.getUserPrompt();

            if (systemPrompt != null
                    && !systemPrompt.trim().isEmpty()) {
                body.put("system", systemPrompt);
            }

            JSONArray messages = new JSONArray();

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");

            JSONArray content = new JSONArray();
            JSONObject text = new JSONObject();
            text.put("type", "text");
            text.put(
                    "text",
                    userPrompt == null ? "" : userPrompt
            );

            content.put(text);
            userMessage.put("content", content);
            messages.put(userMessage);

            body.put("messages", messages);

            Map<String, String> headers =
                    new LinkedHashMap<>();

            headers.put(
                    "x-api-key",
                    getConfig().getApiKey()
            );
            headers.put(
                    "anthropic-version",
                    "2023-06-01"
            );
            headers.put(
                    "Content-Type",
                    "application/json"
            );

            AITransportRequest transportRequest =
                    new AITransportRequest(
                            getEndpoint(),
                            "POST",
                            headers,
                            body.toString()
                    );

            AITransportResponse response =
                    transport.execute(transportRequest);

            if (!response.isSuccess()) {
                return AIResponse.failure(
                        "Claude HTTP error: "
                                + response.getStatusCode()
                                + " "
                                + response.getError(),
                        response.getStatusCode()
                );
            }

            String output =
                    extractOutputText(response.getBody());

            if (output == null
                    || output.trim().isEmpty()) {
                return AIResponse.failure(
                        "Claude returned no text output",
                        -52
                );
            }

            return AIResponse.success(output);

        } catch (Exception e) {
            return AIResponse.failure(
                    "Claude response parsing failed: "
                            + e.getMessage(),
                    -53
            );
        }
    }

    private String getEndpoint() {
        String endpoint =
                getConfig().getEndpoint();

        if (endpoint == null
                || endpoint.trim().isEmpty()) {
            return DEFAULT_ENDPOINT;
        }

        return endpoint;
    }

    private String extractOutputText(String raw) {
        try {
            JSONObject root =
                    new JSONObject(raw);

            JSONArray content =
                    root.optJSONArray("content");

            if (content == null) {
                return "";
            }

            StringBuilder text =
                    new StringBuilder();

            for (int i = 0;
                    i < content.length();
                    i++) {

                JSONObject item =
                        content.optJSONObject(i);

                if (item == null) {
                    continue;
                }

                if (!"text".equals(
                        item.optString("type"))) {
                    continue;
                }

                String value =
                        item.optString("text", "");

                if (!value.isEmpty()) {
                    if (text.length() > 0) {
                        text.append('\n');
                    }

                    text.append(value);
                }
            }

            return text.toString();

        } catch (Exception e) {
            return "";
        }
    }
}
