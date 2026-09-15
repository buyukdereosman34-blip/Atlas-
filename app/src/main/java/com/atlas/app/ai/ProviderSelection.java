package com.atlas.app.ai;

public class ProviderSelection {

    private final boolean success;
    private final AIProvider provider;
    private final String reason;

    private ProviderSelection(
            boolean success,
            AIProvider provider,
            String reason
    ) {
        this.success = success;
        this.provider = provider;
        this.reason = reason;
    }

    public static ProviderSelection selected(
            AIProvider provider
    ) {
        return new ProviderSelection(
                true,
                provider,
                ""
        );
    }

    public static ProviderSelection unavailable(
            String reason
    ) {
        return new ProviderSelection(
                false,
                null,
                reason
        );
    }

    public boolean isSuccess() {
        return success;
    }

    public AIProvider getProvider() {
        return provider;
    }

    public String getReason() {
        return reason;
    }
}
