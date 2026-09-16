package com.atlas.app.ai;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public final class OpenAIProvider extends CloudAIProviderBase {

    public static final String DEFAULT_ENDPOINT =
            "https://api.openai.com/v1/responses";

    private final AIProviderTransport transport;

    public OpenAIProvider(
            AIProviderConfig config,
            AIProviderTransport transport
    ) {
        super(config);
        this.transport = transport;
    }

    @Override
    public String getName() {
        return "openai";
    }

    @Override
    public AIResponse generate(AIRequest request) {
        if (!isAvailable()) {
            return unavailable();
        }

        if (request == null) {
            return AIResponse.failure(
                    "OpenAI request is null",
                    -30
            );
        }

        if (transport == null) {
            return AIResponse.failure(
                    "OpenAI transport is unavailable",
                    -31
            );
        }

        try {
            String systemPrompt = request.getSystemPrompt();
            String userPrompt = request.getUserPrompt();

            JSONObject body = new JSONObject();

            body.put("model", getConfig().getModel());

            JSONArray input = new JSONArray();

            if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                JSONObject system = new JSONObject();
                system.put("role", "developer");
                system.put("content", systemPrompt);
                input.put(system);
            }

            JSONObject user = new JSONObject();
            user.put("role", "user");
            user.put("content", userPrompt == null ? "" : userPrompt);
            input.put(user);

            body.put("input", input);

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
                        "OpenAI HTTP error: "
                                + response.getStatusCode()
                                + " "
                                + response.getError(),
                        response.getStatusCode()
                );
            }

            String output = extractOutputText(response.getBody());

            if (output == null || output.trim().isEmpty()) {
                return AIResponse.failure(
                        "OpenAI returned no text output",
                        -32
                );
            }

            return AIResponse.success(output);

        } catch (Exception e) {
            return AIResponse.failure(
                    "OpenAI response parsing failed: "
                            + e.getMessage(),
                    -33
            );
        }
    }

    private String getEndpoint() {
        String endpoint = getConfig().getEndpoint();

        if (endpoint == null || endpoint.trim().isEmpty()) {
            return DEFAULT_ENDPOINT;
        }

        return endpoint;
    }

    private String extractOutputText(String raw) {
        try {

        JSONObject root = new JSONObject(raw);

        if (root.has("output_text")) {
            return root.optString("output_text", "");
        }

        JSONArray output = root.optJSONArray("output");

        if (output == null) {
            return "";
        }

        StringBuilder text = new StringBuilder();

        for (int i = 0; i < output.length(); i++) {
            JSONObject item = output.optJSONObject(i);

            if (item == null) {
                continue;
            }

            JSONArray content = item.optJSONArray("content");

            if (content == null) {
                continue;
            }

            for (int j = 0; j < content.length(); j++) {
                JSONObject contentItem = content.optJSONObject(j);

                if (contentItem == null) {
                    continue;
                }

                if ("output_text".equals(
                        contentItem.optString("type")
                )) {
                    String value =
                            contentItem.optString("text", "");

                    if (!value.isEmpty()) {
                        if (text.length() > 0) {
                            text.append('\n');
                        }

                        text.append(value);
                    }
                }
            }
        }

        return text.toString();
    
        } catch (Exception e) {
            return "";
        }
    
}
}
