package com.atlas.app.improvement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ImprovementChangeProposal {

    private final String what;
    private final String why;
    private final List<String> affectedFiles;
    private final String expectedResult;
    private final String risk;
    private final boolean reversible;
    private final ImprovementValidation validation;
    private final Map<String, String> proposedFileContents;

    private ImprovementChangeProposalStatus status =
            ImprovementChangeProposalStatus.PROPOSED;

    public ImprovementChangeProposal(
            String what,
            String why,
            List<String> affectedFiles,
            String expectedResult,
            String risk,
            boolean reversible,
            ImprovementValidation validation
    ) {
        this(
                what,
                why,
                affectedFiles,
                expectedResult,
                risk,
                reversible,
                validation,
                Collections.emptyMap()
        );
    }

    public ImprovementChangeProposal(
            String what,
            String why,
            List<String> affectedFiles,
            String expectedResult,
            String risk,
            boolean reversible,
            ImprovementValidation validation,
            Map<String, String> proposedFileContents
    ) {
        this.what = what;
        this.why = why;
        this.affectedFiles =
                affectedFiles == null
                        ? Collections.emptyList()
                        : Collections.unmodifiableList(
                                new ArrayList<>(affectedFiles)
                        );
        this.expectedResult = expectedResult;
        this.risk = risk;
        this.reversible = reversible;
        this.validation = validation;
        this.proposedFileContents =
                proposedFileContents == null
                        ? Collections.emptyMap()
                        : Collections.unmodifiableMap(
                                new java.util.HashMap<>(
                                        proposedFileContents
                                )
                        );
    }

    public String getWhat() {
        return what;
    }

    public String getWhy() {
        return why;
    }

    public List<String> getAffectedFiles() {
        return affectedFiles;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public String getRisk() {
        return risk;
    }

    public boolean isReversible() {
        return reversible;
    }

    public ImprovementValidation getValidation() {
        return validation;
    }

    public Map<String, String> getProposedFileContents() {
        return proposedFileContents;
    }

    public ImprovementChangeProposalStatus getStatus() {
        return status;
    }

    public void approve() {
        if (status == ImprovementChangeProposalStatus.PROPOSED) {
            status = ImprovementChangeProposalStatus.APPROVED;
        }
    }

    public void reject() {
        if (status == ImprovementChangeProposalStatus.PROPOSED) {
            status = ImprovementChangeProposalStatus.REJECTED;
        }
    }
}
