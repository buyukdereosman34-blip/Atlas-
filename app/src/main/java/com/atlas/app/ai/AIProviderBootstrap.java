package com.atlas.app.ai;

public final class AIProviderBootstrap {

    private AIProviderBootstrap() {
    }

    public static AIProviderRegistry createRegistry() {
        AIProviderRegistry registry =
                new AIProviderRegistry();

        AIProviderTransport transport =
                new HttpAIProviderTransport();

        AIProviderConfig openAIConfig =
                AIProviderConfigLoader.load(
                        "openai",
                        OpenAIProvider.DEFAULT_ENDPOINT,
                        ""
                );

        AIProviderConfig geminiConfig =
                AIProviderConfigLoader.load(
                        "gemini",
                        GeminiProvider.DEFAULT_ENDPOINT,
                        ""
                );

        AIProviderConfig claudeConfig =
                AIProviderConfigLoader.load(
                        "claude",
                        ClaudeProvider.DEFAULT_ENDPOINT,
                        ""
                );

        AIProviderConfig ollamaConfig =
                AIProviderConfigLoader.load(
                        "ollama",
                        OllamaProvider.DEFAULT_ENDPOINT,
                        ""
                );

        AIProviderConfig groqConfig =
                AIProviderConfigLoader.load(
                        "groq",
                        "https://api.groq.com/openai/v1/chat/completions",
                        "llama-3.3-70b-versatile"
                );

        AIProviderConfig openRouterConfig =
                AIProviderConfigLoader.load(
                        "openrouter",
                        "https://openrouter.ai/api/v1/chat/completions",
                        "meta-llama/llama-3.2-3b-instruct:free"
                );

        AIProviderConfig cerebrasConfig =
                AIProviderConfigLoader.load(
                        "cerebras",
                        "https://api.cerebras.ai/v1/chat/completions",
                        "llama3.1-8b"
                );

        registry.register(
                new OpenAIProvider(
                        openAIConfig,
                        transport
                )
        );

        registry.register(
                new GeminiProvider(
                        geminiConfig,
                        transport
                )
        );

        registry.register(
                new ClaudeProvider(
                        claudeConfig,
                        transport
                )
        );

        registry.register(
                new OllamaProvider(
                        ollamaConfig,
                        transport
                )
        );

        registry.register(
                new OpenAICompatibleProvider(
                        "groq",
                        "https://api.groq.com/openai/v1/chat/completions",
                        groqConfig,
                        transport
                )
        );

        registry.register(
                new OpenAICompatibleProvider(
                        "openrouter",
                        "https://openrouter.ai/api/v1/chat/completions",
                        openRouterConfig,
                        transport
                )
        );

        registry.register(
                new OpenAICompatibleProvider(
                        "cerebras",
                        "https://api.cerebras.ai/v1/chat/completions",
                        cerebrasConfig,
                        transport
                )
        );

        return registry;
    }

    public static AIProviderRouter createRouter() {
        return new AIProviderRouter(
                createRegistry()
        );
    }
}
