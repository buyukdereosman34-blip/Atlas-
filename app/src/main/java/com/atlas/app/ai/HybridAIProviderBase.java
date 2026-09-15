package com.atlas.app.ai;

public abstract class HybridAIProviderBase
        extends HybridAIProvider {

    protected final AIProvider cloudProvider;
    protected final AIProvider localProvider;

    protected HybridAIProviderBase(
            AIProvider cloudProvider,
            AIProvider localProvider
    ) {
        this.cloudProvider = cloudProvider;
        this.localProvider = localProvider;
    }

    @Override
    public boolean isAvailable() {
        return isAvailable(cloudProvider)
                || isAvailable(localProvider);
    }

    protected AIProvider selectProvider() {
        if (isAvailable(cloudProvider)) {
            return cloudProvider;
        }

        if (isAvailable(localProvider)) {
            return localProvider;
        }

        return null;
    }

    private boolean isAvailable(
            AIProvider provider
    ) {
        return provider != null
                && provider.isAvailable();
    }
}
