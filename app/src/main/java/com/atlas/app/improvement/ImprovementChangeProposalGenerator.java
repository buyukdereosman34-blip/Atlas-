package com.atlas.app.improvement;

import com.atlas.app.codegen.CodeChangeSet;
import com.atlas.app.codegen.CodeGenerationEngine;
import com.atlas.app.codegen.CodeGenerationRequest;
import com.atlas.app.codegen.RuleBasedCodeGenerator;

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
