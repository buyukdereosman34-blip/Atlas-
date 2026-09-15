package com.atlas.app.codegen;

public class CodeGenerationEngine {

    private final CodeGenerator generator;

    public CodeGenerationEngine(CodeGenerator generator) {
        this.generator = generator;
    }

    public CodeChangeSet generate(
            CodeGenerationRequest request
    ) {
        if (generator == null || request == null) {
            return new CodeChangeSet(null);
        }

        return generator.generate(request);
    }
}
