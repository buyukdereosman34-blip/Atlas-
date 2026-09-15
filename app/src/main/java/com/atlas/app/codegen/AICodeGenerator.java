package com.atlas.app.codegen;

import com.atlas.app.ai.AIProvider;
import com.atlas.app.ai.AIRequest;
import com.atlas.app.ai.AIResponse;

public class AICodeGenerator implements CodeGenerator {

    private final AIProvider provider;

    public AICodeGenerator(AIProvider provider) {
        this.provider = provider;
    }

    @Override
    public CodeChangeSet generate(
            CodeGenerationRequest request
    ) {
        if (request == null || provider == null) {
            return new CodeChangeSet(null);
        }

        if (!provider.isAvailable()) {
            return new CodeChangeSet(null);
        }

        String prompt =
                buildPrompt(request);

        AIResponse response =
                provider.generate(
                        new AIRequest(
                                "You are the ATLAS code generation engine. "
                                        + "Return safe, minimal source-code changes.",
                                prompt
                        )
                );

        if (response == null
                || !response.isSuccess()) {
            return new CodeChangeSet(null);
        }

        /*
         * The provider contract is intentionally separated
         * from CodeChangeSet parsing.
         *
         * A structured AI response parser will be added
         * when real providers are connected.
         */
        return new CodeChangeSet(null);
    }

    private String buildPrompt(
            CodeGenerationRequest request
    ) {
        StringBuilder prompt =
                new StringBuilder();

        prompt.append("Task:\n")
                .append(request.getTaskDescription())
                .append("\n\n");

        prompt.append("Source path:\n")
                .append(request.getSourcePath())
                .append("\n\n");

        prompt.append("Affected files:\n");

        for (String file :
                request.getAffectedFiles()) {

            if (file != null
                    && !file.trim().isEmpty()) {
                prompt.append("- ")
                        .append(file)
                        .append('\n');
            }
        }

        return prompt.toString();
    }
}
