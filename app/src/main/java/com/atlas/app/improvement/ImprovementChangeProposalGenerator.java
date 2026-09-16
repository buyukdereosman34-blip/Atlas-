package com.atlas.app.improvement;

import com.atlas.app.codegen.CodeChangeSet;
import com.atlas.app.codegen.CodeGenerationEngine;
import com.atlas.app.codegen.CodeGenerationRequest;
import com.atlas.app.codegen.CodeGenerator;
import com.atlas.app.codegen.RouterAICodeGenerator;
import com.atlas.app.codegen.RuleBasedCodeGenerator;
import com.atlas.app.ai.AIProviderRouter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ImprovementChangeProposalGenerator {

    private final CodeGenerationEngine codeGenerationEngine;

    public ImprovementChangeProposalGenerator() {
        this.codeGenerationEngine =
                new CodeGenerationEngine(
                        new RuleBasedCodeGenerator()
                );
    }

    public ImprovementChangeProposalGenerator(AIProviderRouter router) {
        CodeGenerator fallback = new RuleBasedCodeGenerator();

        this.codeGenerationEngine =
                new CodeGenerationEngine(
                        new RouterAICodeGenerator(
                                router,
                                fallback
                        )
                );
    }

    public ImprovementChangeProposal generate(
            ImprovementTask task,
            ImprovementFinding finding,
            List<String> affectedFiles
    ) {
        return generate(
                task,
                finding,
                affectedFiles,
                null
        );
    }

    public ImprovementChangeProposal generate(
            ImprovementTask task,
            ImprovementFinding finding,
            List<String> affectedFiles,
            String sourcePath
    ) {
        if (task == null || finding == null) {
            return null;
        }

        ImprovementValidation validation =
                new ImprovementValidation(
                        "Build and test the proposed change",
                        "The proposed change passes validation without introducing regressions"
                );

        String what =
                "Improve " + finding.getArea()
                        + " by addressing: "
                        + finding.getProblem();

        String why =
                task.getDescription();

        String expectedResult =
                finding.getRecommendation();

        List<String> files =
                affectedFiles == null
                        ? Collections.emptyList()
                        : affectedFiles;

        CodeGenerationRequest request =
                new CodeGenerationRequest(
                        task.getDescription(),
                        sourcePath,
                        files
                );

        CodeChangeSet changeSet =
                codeGenerationEngine.generate(request);

        Map<String, String> proposedFileContents =
                changeSet.getFileContents();

        return new ImprovementChangeProposal(
                what,
                why,
                files,
                expectedResult,
                "MEDIUM",
                true,
                validation,
                proposedFileContents
        );
    }
}
