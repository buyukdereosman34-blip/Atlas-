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

        appendSourceTree(prompt, request);

        return prompt.toString();
    }

    private void appendSourceTree(
            StringBuilder prompt,
            CodeGenerationRequest request
    ) {
        String sourcePath = request.getSourcePath();

        if (sourcePath == null
                || sourcePath.trim().isEmpty()) {
            return;
        }

        java.io.File sourceRoot =
                new java.io.File(sourcePath);

        if (!sourceRoot.isDirectory()) {
            return;
        }

        prompt.append("\nATLAS source tree:\n");

        java.util.List<String> paths =
                new java.util.ArrayList<>();

        collectSourcePaths(
                sourceRoot,
                sourceRoot,
                paths
        );

        java.util.Collections.sort(paths);

        for (String relativePath : paths) {
            prompt.append("- ")
                    .append(relativePath)
                    .append('\n');
        }

        prompt.append(
                "\nYou may create new files when the requested feature does not exist. "
                + "You may modify existing files when integration requires it. "
                + "Return every created or modified file in the structured format.\n"
        );
    }

    private void collectSourcePaths(
            java.io.File root,
            java.io.File directory,
            java.util.List<String> result
    ) {
        java.io.File[] children =
                directory.listFiles();

        if (children == null) {
            return;
        }

        for (java.io.File child : children) {
            if (child.isDirectory()) {
                String name = child.getName();

                if ("build".equals(name)
                        || ".gradle".equals(name)
                        || ".git".equals(name)) {
                    continue;
                }

                collectSourcePaths(
                        root,
                        child,
                        result
                );
                continue;
            }

            String name = child.getName();

            if (name.endsWith(".java")
                    || name.endsWith(".kt")) {
                try {
                    result.add(
                            root.toPath()
                                    .relativize(child.toPath())
                                    .toString()
                    );
                } catch (Exception ignored) {
                    // Skip paths that cannot be relativized.
                }
            }
        }
    }
}
