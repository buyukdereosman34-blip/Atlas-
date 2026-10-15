package com.atlas.app.ai;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generic provider for OpenAI-compatible Chat Completions API.
 *
 * Supports providers that implement:
 *   POST /v1/chat/completions
 *   Authorization: Bearer <key>
 *   Body: {"model": "...", "messages": [...]}
 *
 * Tested against:
 *   - Groq     (api.groq.com)
 *   - OpenRouter (openrouter.ai/api/v1)
 *   - Cerebras (api.cerebras.ai)
 */
public final class OpenAICompatibleProvider extends CloudAIProviderBase {

    private final String name;
    private final String defaultEndpoint;
    private final AIProviderTransport transport;

    public OpenAICompatibleProvider(
            String name,
            String defaultEndpoint,
            AIProviderConfig config,
            AIProviderTransport transport
    ) {
        super(config);
        this.name = name == null ? "openai-compatible" : name;
        this.defaultEndpoint = defaultEndpoint;
        this.transport = transport;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AIResponse generate(AIRequest request) {
        if (!isAvailable()) {
            return unavailable();
        }

        if (request == null) {
            return AIResponse.failure(
                    name + " request is null",
                    -70
            );
        }

        if (transport == null) {
            return AIResponse.failure(
                    name + " transport is unavailable",
                    -71
            );
        }

        try {
            String systemPrompt = request.getSystemPrompt();
            String userPrompt = request.getUserPrompt();

            JSONObject body = new JSONObject();
            body.put("model", getConfig().getModel());

            JSONArray messages = new JSONArray();

            if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                JSONObject system = new JSONObject();
                system.put("role", "system");
                system.put("content", systemPrompt);
                messages.put(system);
            }

            JSONObject user = new JSONObject();
            user.put("role", "user");
            user.put("content", userPrompt == null ? "" : userPrompt);
            messages.put(user);

            body.put("messages", messages);

            Map<String, String> headers = new LinkedHashMap<>();
            headers.put(
                    "Authorization",
                    "Bearer " + getConfig().getApiKey()
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
                        name + " HTTP error: "
                                + response.getStatusCode()
                                + " "
                                + response.getError(),
                        response.getStatusCode()
                );
            }

            String output = extractOutputText(response.getBody());

            if (output == null || output.trim().isEmpty()) {
                return AIResponse.failure(
                        name + " returned no text output",
                        -72
                );
            }

            return AIResponse.success(output);

        } catch (Exception e) {
            return AIResponse.failure(
                    name + " parsing failed: " + e.getMessage(),
                    -73
            );
        }
    }

    private String getEndpoint() {
        String endpoint = getConfig().getEndpoint();

        if (endpoint == null || endpoint.trim().isEmpty()) {
            return defaultEndpoint;
        }

        return endpoint;
    }

    private String extractOutputText(String raw) {
        try {
            JSONObject root = new JSONObject(raw);

            JSONArray choices = root.optJSONArray("choices");

            if (choices == null || choices.length() == 0) {
                return "";
            }

            JSONObject first = choices.optJSONObject(0);

            if (first == null) {
                return "";
            }

            JSONObject message = first.optJSONObject("message");

            if (message == null) {
                return "";
            }

            return message.optString("content", "");

        } catch (Exception e) {
            return "";
        }
    }
}
