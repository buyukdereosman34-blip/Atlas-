package com.atlas.app.ai;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public final class OllamaProvider extends LocalAIProviderBase {

    public static final String DEFAULT_ENDPOINT =
            "http://127.0.0.1:11434/api/chat";

    private final AIProviderTransport transport;

    public OllamaProvider(
            AIProviderConfig config,
            AIProviderTransport transport
    ) {
        super(config);
        this.transport = transport;
    }

    @Override
    public String getName() {
        return "ollama";
    }

    @Override
    public AIResponse generate(AIRequest request) {
        if (!isAvailable()) {
            return unavailable();
        }

        if (request == null) {
            return AIResponse.failure(
                    "Ollama request is null",
                    -60
            );
        }

        if (transport == null) {
            return AIResponse.failure(
                    "Ollama transport is unavailable",
                    -61
            );
        }

        try {
            JSONObject body = new JSONObject();

            body.put("model", getConfig().getModel());
            body.put("stream", false);

            JSONArray messages = new JSONArray();

            String systemPrompt = request.getSystemPrompt();
            String userPrompt = request.getUserPrompt();

            if (systemPrompt != null
                    && !systemPrompt.trim().isEmpty()) {

                JSONObject system = new JSONObject();
                system.put("role", "system");
                system.put("content", systemPrompt);
                messages.put(system);
            }

            JSONObject user = new JSONObject();
            user.put("role", "user");
            user.put(
                    "content",
                    userPrompt == null ? "" : userPrompt
            );
            messages.put(user);

            body.put("messages", messages);

            Map<String, String> headers =
                    new LinkedHashMap<>();

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
                        "Ollama HTTP error: "
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
                        "Ollama returned no text output",
                        -62
                );
            }

            return AIResponse.success(output);

        } catch (Exception e) {
            return AIResponse.failure(
                    "Ollama response parsing failed: "
                            + e.getMessage(),
                    -63
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

            JSONObject message =
                    root.optJSONObject("message");

            if (message == null) {
                return "";
            }

            return message.optString(
                    "content",
                    ""
            );

        } catch (Exception e) {
            return "";
        }
    }
}
