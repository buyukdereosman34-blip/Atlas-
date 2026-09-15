package com.atlas.app.ai;

public interface AIProvider {

    String getName();

    AIProviderType getType();

    boolean isAvailable();

    AIResponse generate(
            AIRequest request
    );
}
