package com.atlas.app.ai;

public class AIResponse {

    private final boolean success;
    private final String output;
    private final int code;

    private AIResponse(
            boolean success,
            String output,
            int code
    ) {
        this.success = success;
        this.output = output == null ? "" : output;
        this.code = code;
    }

    public static AIResponse success(String output) {
        return new AIResponse(
                true,
                output,
                0
        );
    }

    public static AIResponse failure(
            String output,
            int code
    ) {
        return new AIResponse(
                false,
                output,
                code
        );
    }

    public boolean isSuccess() {
        return success;
    }

    public String getOutput() {
        return output;
    }

    public int getCode() {
        return code;
    }
}
