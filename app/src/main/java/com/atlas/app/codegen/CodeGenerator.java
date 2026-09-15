package com.atlas.app.codegen;

public interface CodeGenerator {

    CodeChangeSet generate(
            CodeGenerationRequest request
    );
}
