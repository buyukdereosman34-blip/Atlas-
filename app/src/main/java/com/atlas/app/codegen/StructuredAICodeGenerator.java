package com.atlas.app.codegen;

import com.atlas.app.ai.AIProvider;
import com.atlas.app.ai.AIRequest;
import com.atlas.app.ai.AIResponse;

public class StructuredAICodeGenerator
        implements CodeGenerator {

    private final AIProvider provider;
    private final CodeChangeParser parser;

    public StructuredAICodeGenerator(
            AIProvider provider
    ) {
        this.provider = provider;
        this.parser =
                new CodeChangeParser();
    }

    @Override
    public CodeChangeSet generate(
            CodeGenerationRequest request
    ) {
        if (request == null
                || provider == null
                || !provider.isAvailable()) {
            return new CodeChangeSet(null);
        }

        AIResponse response =
                provider.generate(
                        new AIRequest(
                                buildSystemPrompt(),
                                buildUserPrompt(request)
                        )
                );

        if (response == null
                || !response.isSuccess()) {
            return new CodeChangeSet(null);
        }

        return parser.parse(
                response.getOutput()
        );
    }

    private String buildSystemPrompt() {
        return "You are the ATLAS code generation engine. "
                + "Return only structured file changes. "
                + "For every file use exactly this format:\n"
                + "---FILE---\n"
                + "relative/path/to/file\n"
                + "---CONTENT---\n"
                + "complete file content\n"
                + "Do not include explanations.";
    }

    private String buildUserPrompt(
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
