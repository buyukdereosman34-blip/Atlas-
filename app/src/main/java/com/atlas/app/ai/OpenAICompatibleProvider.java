package com.atlas.app.ai;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic provider for OpenAI-compatible Chat Completions API.
 *
 * Supports:
 *   POST /v1/chat/completions
 *
 * Model resolution:
 *   1. If config has a model -> use it
 *   2. Otherwise -> call /v1/models, pick best match
 *   3. Cache result; on 404 -> clear cache and retry once
 *
 * Priority list for auto-discovery: gpt-oss, qwen3, llama-3.3, etc.
 */
public final class OpenAICompatibleProvider extends CloudAIProviderBase {

    private final String name;
    private final String defaultEndpoint;
    private final AIProviderTransport transport;

    private volatile String resolvedModel = null;

    private static final List<String> MODEL_PRIORITY = Arrays.asList(
            "gpt-oss-120b",
            "gpt-oss-20b",
            "gpt-oss",
            "qwen3",
            "qwen2.5",
            "llama-3.3",
            "llama-3.1",
            "llama-3",
            "mixtral"
    );

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
    public boolean isAvailable() {
        if (getConfig() == null) {
            return false;
        }

        String endpoint = getConfig().getEndpoint();
        String apiKey = getConfig().getApiKey();

        return endpoint != null && !endpoint.trim().isEmpty()
                && apiKey != null && !apiKey.trim().isEmpty();
    }

    @Override
    public AIResponse generate(AIRequest request) {
        if (!isAvailable()) {
            return unavailable();
        }

        if (request == null) {
            return AIResponse.failure(name + " request is null", -70);
        }

        if (transport == null) {
            return AIResponse.failure(name + " transport is unavailable", -71);
        }

        try {
            String model = resolveModel();

            if (model == null || model.trim().isEmpty()) {
                return AIResponse.failure(
                        name + " has no available model",
                        -74
                );
            }

            String systemPrompt = request.getSystemPrompt();
            String userPrompt = request.getUserPrompt();

            JSONObject body = new JSONObject();
            body.put("model", model);

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
            headers.put("Authorization", "Bearer " + getConfig().getApiKey());
            headers.put("Content-Type", "application/json");

            AITransportResponse response = sendChat(headers, body);

            // 404 -> model changed; clear cache and retry once
            if (!response.isSuccess()
                    && response.getStatusCode() == 404
                    && resolvedModel != null) {

                resolvedModel = null;

                String newModel = resolveModel();

                if (newModel != null && !newModel.equals(model)) {
                    body.put("model", newModel);
                    response = sendChat(headers, body);
                }
            }

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

    private AITransportResponse sendChat(
            Map<String, String> headers,
            JSONObject body
    ) {
        AITransportRequest req = new AITransportRequest(
                getEndpoint(),
                "POST",
                headers,
                body.toString()
        );

        return transport.execute(req);
    }

    private String resolveModel() {
        if (resolvedModel != null) {
            return resolvedModel;
        }

        String configured = getConfig().getModel();

        if (configured != null && !configured.trim().isEmpty()) {
            resolvedModel = configured.trim();
            return resolvedModel;
        }

        try {
            List<String> models = fetchAvailableModels();
            String chosen = chooseModel(models);

            if (chosen != null) {
                resolvedModel = chosen;
                return resolvedModel;
            }
        } catch (Exception ignored) {
            // Discovery failed
        }

        return null;
    }

    private List<String> fetchAvailableModels() {
        List<String> result = new ArrayList<>();

        try {
            String modelsUrl = deriveModelsUrl();

            Map<String, String> headers = new LinkedHashMap<>();
            headers.put("Authorization", "Bearer " + getConfig().getApiKey());

            AITransportRequest req = new AITransportRequest(
                    modelsUrl,
                    "GET",
                    headers,
                    null
            );

            AITransportResponse resp = transport.execute(req);

            if (!resp.isSuccess()) {
                return result;
            }

            JSONObject root = new JSONObject(resp.getBody());
            JSONArray data = root.optJSONArray("data");

            if (data == null) {
                return result;
            }

            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.optJSONObject(i);

                if (item == null) {
                    continue;
                }

                String id = item.optString("id", "");

                if (!id.isEmpty()) {
                    result.add(id);
                }
            }
        } catch (Exception ignored) {
            // Return empty
        }

        return result;
    }

    private String chooseModel(List<String> models) {
        if (models == null || models.isEmpty()) {
            return null;
        }

        for (String priority : MODEL_PRIORITY) {
            for (String model : models) {
                if (model.toLowerCase().contains(priority.toLowerCase())) {
                    return model;
                }
            }
        }

        return models.get(0);
    }

    private String deriveModelsUrl() {
        String endpoint = getEndpoint();

        int idx = endpoint.lastIndexOf("/chat/completions");

        if (idx > 0) {
            return endpoint.substring(0, idx) + "/models";
        }

        return endpoint + "/models";
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
