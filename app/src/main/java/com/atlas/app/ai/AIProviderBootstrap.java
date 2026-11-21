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
                        ""
                );

        AIProviderConfig openRouterConfig =
                AIProviderConfigLoader.load(
                        "openrouter",
                        "https://openrouter.ai/api/v1/chat/completions",
                        ""
                );

        AIProviderConfig cerebrasConfig =
                AIProviderConfigLoader.load(
                        "cerebras",
                        "https://api.cerebras.ai/v1/chat/completions",
                        ""
                );

        AIProviderConfig mistralConfig =
                AIProviderConfigLoader.load(
                        "mistral",
                        "https://api.mistral.ai/v1/chat/completions",
                        ""
                );

        AIProviderConfig huggingFaceConfig =
                AIProviderConfigLoader.load(
                        "huggingface",
                        "https://api-inference.huggingface.co/v1/chat/completions",
                        ""
                );

        AIProviderConfig togetherConfig =
                AIProviderConfigLoader.load(
                        "together",
                        "https://api.together.xyz/v1/chat/completions",
                        ""
                );

        AIProviderConfig deepSeekConfig =
                AIProviderConfigLoader.load(
                        "deepseek",
                        "https://api.deepseek.com/v1/chat/completions",
                        ""
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

        registry.register(
                new OpenAICompatibleProvider(
                        "mistral",
                        "https://api.mistral.ai/v1/chat/completions",
                        mistralConfig,
                        transport
                )
        );

        registry.register(
                new OpenAICompatibleProvider(
                        "huggingface",
                        "https://api-inference.huggingface.co/v1/chat/completions",
                        huggingFaceConfig,
                        transport
                )
        );

        registry.register(
                new OpenAICompatibleProvider(
                        "together",
                        "https://api.together.xyz/v1/chat/completions",
                        togetherConfig,
                        transport
                )
        );

        registry.register(
                new OpenAICompatibleProvider(
                        "deepseek",
                        "https://api.deepseek.com/v1/chat/completions",
                        deepSeekConfig,
                        transport
                )
        );

        // OmniRoute: öncelik sırasıyla dener
        OmniRouteProvider omni = new OmniRouteProvider();
        omni.add(registry.get("groq"));
        omni.add(registry.get("gemini"));
        omni.add(registry.get("openrouter"));
        omni.add(registry.get("cerebras"));
        omni.add(registry.get("mistral"));
        omni.add(registry.get("deepseek"));
        registry.register(omni);

        return registry;
    }

    public static AIProviderRouter createRouter() {
        return new AIProviderRouter(
                createRegistry()
        );
    }
}
