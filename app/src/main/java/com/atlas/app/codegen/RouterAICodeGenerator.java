package com.atlas.app.codegen;

import com.atlas.app.ai.AITaskType;
import com.atlas.app.ai.AIProvider;
import com.atlas.app.ai.AIProviderRouter;
import com.atlas.app.ai.ProviderSelection;

public class RouterAICodeGenerator implements CodeGenerator {

    private final AIProviderRouter router;
    private final CodeGenerator fallbackGenerator;

    public RouterAICodeGenerator(
            AIProviderRouter router,
            CodeGenerator fallbackGenerator
    ) {
        this.router = router;
        this.fallbackGenerator = fallbackGenerator;
    }

    @Override
    public CodeChangeSet generate(CodeGenerationRequest request) {
        if (request == null) {
            return new CodeChangeSet(null);
        }

        if (router != null) {
            ProviderSelection selection =
                    router.route(AITaskType.CODING);

            if (selection != null && selection.isSuccess()) {
                AIProvider provider = selection.getProvider();

                if (provider != null && provider.isAvailable()) {
                    CodeChangeSet aiChanges =
                            new StructuredAICodeGenerator(provider)
                                    .generate(request);

                    if (aiChanges != null && !aiChanges.isEmpty()) {
                        return aiChanges;
                    }
                }
            }
        }

        if (fallbackGenerator != null) {
            return fallbackGenerator.generate(request);
        }

        return new CodeChangeSet(null);
    }
}
