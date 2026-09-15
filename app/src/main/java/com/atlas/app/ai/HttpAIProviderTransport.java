package com.atlas.app.ai;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class HttpAIProviderTransport implements AIProviderTransport {

    private final int connectTimeoutMs;
    private final int readTimeoutMs;

    public HttpAIProviderTransport() {
        this(15000, 60000);
    }

    public HttpAIProviderTransport(
            int connectTimeoutMs,
            int readTimeoutMs
    ) {
        this.connectTimeoutMs = connectTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
    }

    @Override
    public AITransportResponse execute(AITransportRequest request) {
        if (request == null || request.getEndpoint() == null
                || request.getEndpoint().trim().isEmpty()) {
            return AITransportResponse.failure(
                    -1,
                    "",
                    "Transport endpoint is empty"
            );
        }

        HttpURLConnection connection = null;

        try {
            URL url = new URL(request.getEndpoint());
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(request.getMethod());
            connection.setConnectTimeout(connectTimeoutMs);
            connection.setReadTimeout(readTimeoutMs);
            connection.setDoInput(true);

            for (Map.Entry<String, String> header
                    : request.getHeaders().entrySet()) {
                connection.setRequestProperty(
                        header.getKey(),
                        header.getValue()
                );
            }

            String body = request.getBody();

            if (body != null && !body.isEmpty()) {
                connection.setDoOutput(true);

                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

                try (OutputStream output = connection.getOutputStream()) {
                    output.write(bytes);
                }
            }

            int statusCode = connection.getResponseCode();

            InputStream stream = statusCode >= 400
                    ? connection.getErrorStream()
                    : connection.getInputStream();

            String responseBody = read(stream);

            if (statusCode >= 200 && statusCode < 300) {
                return AITransportResponse.success(
                        statusCode,
                        responseBody
                );
            }

            return AITransportResponse.failure(
                    statusCode,
                    responseBody,
                    "HTTP " + statusCode
            );

        } catch (Exception e) {
            return AITransportResponse.failure(
                    -1,
                    "",
                    e.getClass().getSimpleName() + ": " + e.getMessage()
            );

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String read(InputStream stream) throws Exception {
        if (stream == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        stream,
                        StandardCharsets.UTF_8
                ))) {

            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line).append('\n');
            }
        }

        return result.toString().trim();
    }
}
