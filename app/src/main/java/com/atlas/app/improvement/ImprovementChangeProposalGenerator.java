package com.atlas.app.improvement;

import java.util.Collections;
import java.util.List;

public class ImprovementChangeProposalGenerator {

    public ImprovementChangeProposal generate(
            ImprovementTask task,
            ImprovementFinding finding,
            List<String> affectedFiles
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
                        + " by addressing: " + finding.getProblem();

        String why =
                task.getDescription();

        String expectedResult =
                finding.getRecommendation();

        List<String> files =
                affectedFiles == null
                        ? Collections.emptyList()
                        : affectedFiles;

        return new ImprovementChangeProposal(
                what,
                why,
                files,
                expectedResult,
                "MEDIUM",
                true,
                validation
        );
    }
}
