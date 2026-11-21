package com.atlas.app.improvement;

import com.atlas.app.ai.AIProvider;
import com.atlas.app.ai.AIProviderBootstrap;
import com.atlas.app.ai.AIProviderRouter;
import com.atlas.app.ai.AITaskType;
import com.atlas.app.ai.ProviderSelection;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RealGeminiProposalTest {

    @Test
    public void geminiCanGenerateRealChangeProposal() {

        String apiKey = System.getenv("ATLAS_AI_GEMINI_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.out.println("[SKIP] Gemini API key yok, test atlaniyor");
            return;
        }


        AIProviderRouter router =
                AIProviderBootstrap.createRouter();

        ProviderSelection selection =
                router.route(AITaskType.CODING);

        assertTrue(
                "No available AI provider",
                selection.isSuccess()
        );

        AIProvider provider =
                selection.getProvider();

        assertNotNull(provider);
        assertTrue(provider.isAvailable());

        ImprovementChangeProposalGenerator generator =
                new ImprovementChangeProposalGenerator(router);

        ImprovementTask task =
                new ImprovementTask(
                        "ATLAS test improvement",
                        "Create a minimal safe Java test change."
                );

        ImprovementFinding finding =
                new ImprovementFinding(
                        "Test",
                        "Create a minimal test file change.",
                        "Create build/atlas-test/Test.java"
                );

        ImprovementChangeProposal proposal =
                generator.generate(
                        task,
                        finding,
                        Collections.singletonList(
                                "build/atlas-test/Test.java"
                        ),
                        "."
                );

        assertNotNull(proposal);
        assertNotNull(proposal.getProposedFileContents());

        assertFalse(
                "Gemini did not produce any structured file changes; fallback may have been used.",
                proposal.getProposedFileContents().isEmpty()
        );

        assertTrue(
                "Gemini output did not contain the requested target file.",
                proposal.getProposedFileContents().containsKey(
                        "build/atlas-test/Test.java"
                )
        );

        String generatedContent =
                proposal.getProposedFileContents().get(
                        "build/atlas-test/Test.java"
                );

        assertNotNull(generatedContent);

        assertTrue(
                "Generated file content is empty.",
                !generatedContent.trim().isEmpty()
        );
    }
}
