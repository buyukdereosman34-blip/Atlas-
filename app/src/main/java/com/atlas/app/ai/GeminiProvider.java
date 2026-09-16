package com.atlas.app.ai;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public final class GeminiProvider extends CloudAIProviderBase {

    public static final String DEFAULT_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models";

    private final AIProviderTransport transport;

    public GeminiProvider(
            AIProviderConfig config,
            AIProviderTransport transport
    ) {
        super(config);
        this.transport = transport;
    }

    @Override
    public String getName() {
        return "gemini";
    }

    @Override
    public AIResponse generate(AIRequest request) {
        if (!isAvailable()) {
            return unavailable();
        }

        if (request == null) {
            return AIResponse.failure(
                    "Gemini request is null",
                    -40
            );
        }

        if (transport == null) {
            return AIResponse.failure(
                    "Gemini transport is unavailable",
                    -41
            );
        }

        try {
            JSONObject body = new JSONObject();

            JSONArray contents = new JSONArray();
            JSONObject userContent = new JSONObject();
            userContent.put("role", "user");

            JSONArray parts = new JSONArray();

            String systemPrompt = request.getSystemPrompt();
            String userPrompt = request.getUserPrompt();

            if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                JSONObject systemPart = new JSONObject();
                systemPart.put(
                        "text",
                        systemPrompt
                );
                parts.put(systemPart);
            }

            JSONObject userPart = new JSONObject();
            userPart.put(
                    "text",
                    userPrompt == null ? "" : userPrompt
            );
            parts.put(userPart);

            userContent.put("parts", parts);
            contents.put(userContent);
            body.put("contents", contents);

            String endpoint = getEndpoint();

            if (!endpoint.contains("?")) {
                endpoint += "?key=" + getConfig().getApiKey();
            } else {
                endpoint += "&key=" + getConfig().getApiKey();
            }

            Map<String, String> headers =
                    new LinkedHashMap<>();

            headers.put(
                    "Content-Type",
                    "application/json"
            );

            AITransportRequest transportRequest =
                    new AITransportRequest(
                            endpoint,
                            "POST",
                            headers,
                            body.toString()
                    );

            AITransportResponse response =
                    transport.execute(transportRequest);

            if (!response.isSuccess()) {
                return AIResponse.failure(
                        "Gemini HTTP error: "
                                + response.getStatusCode()
                                + " "
                                + response.getError(),
                        response.getStatusCode()
                );
            }

            String output =
                    extractOutputText(response.getBody());

            if (output == null || output.trim().isEmpty()) {
                return AIResponse.failure(
                        "Gemini returned no text output",
                        -42
                );
            }

            return AIResponse.success(output);

        } catch (Exception e) {
            return AIResponse.failure(
                    "Gemini response parsing failed: "
                            + e.getMessage(),
                    -43
            );
        }
    }

    private String getEndpoint() {
        String endpoint =
                getConfig().getEndpoint();

        if (endpoint == null
                || endpoint.trim().isEmpty()) {

            String model =
                    getConfig().getModel();

            return DEFAULT_ENDPOINT
                    + "/"
                    + model
                    + ":generateContent";
        }

        return endpoint;
    }

    private String extractOutputText(String raw) {
        try {
            JSONObject root =
                    new JSONObject(raw);

            JSONArray candidates =
                    root.optJSONArray("candidates");

            if (candidates == null) {
                return "";
            }

            StringBuilder text =
                    new StringBuilder();

            for (int i = 0;
                    i < candidates.length();
                    i++) {

                JSONObject candidate =
                        candidates.optJSONObject(i);

                if (candidate == null) {
                    continue;
                }

                JSONObject content =
                        candidate.optJSONObject("content");

                if (content == null) {
                    continue;
                }

                JSONArray parts =
                        content.optJSONArray("parts");

                if (parts == null) {
                    continue;
                }

                for (int j = 0;
                        j < parts.length();
                        j++) {

                    JSONObject part =
                            parts.optJSONObject(j);

                    if (part == null) {
                        continue;
                    }

                    String value =
                            part.optString(
                                    "text",
                                    ""
                            );

                    if (!value.isEmpty()) {
                        if (text.length() > 0) {
                            text.append('\n');
                        }

                        text.append(value);
                    }
                }
            }

            return text.toString();

        } catch (Exception e) {
            return "";
        }
    }
}
