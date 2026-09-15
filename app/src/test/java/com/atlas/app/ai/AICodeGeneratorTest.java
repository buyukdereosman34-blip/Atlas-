package com.atlas.app.ai;

import com.atlas.app.codegen.AICodeGenerator;
import com.atlas.app.codegen.CodeGenerationRequest;
import com.atlas.app.codegen.CodeChangeSet;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class AICodeGeneratorTest {

    @Test
    public void unavailableProviderDoesNotGenerateChanges() {
        AIProvider provider =
                new UnavailableAIProvider(
                        "test",
                        AIProviderType.LOCAL
                );

        AICodeGenerator generator =
                new AICodeGenerator(provider);

        CodeChangeSet result =
                generator.generate(
                        new CodeGenerationRequest(
                                "Add feature",
                                "/tmp/project",
                                Collections.singletonList(
                                        "Test.java"
                                )
                        )
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
