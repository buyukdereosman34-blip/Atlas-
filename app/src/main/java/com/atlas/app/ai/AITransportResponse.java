package com.atlas.app.ai;

public final class AITransportResponse {

    private final boolean success;
    private final int statusCode;
    private final String body;
    private final String error;

    private AITransportResponse(
            boolean success,
            int statusCode,
            String body,
            String error
    ) {
        this.success = success;
        this.statusCode = statusCode;
        this.body = body;
        this.error = error;
    }

    public static AITransportResponse success(int statusCode, String body) {
        return new AITransportResponse(true, statusCode, body, null);
    }

    public static AITransportResponse failure(
            int statusCode,
            String body,
            String error
    ) {
        return new AITransportResponse(false, statusCode, body, error);
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public String getError() {
        return error;
    }
}
