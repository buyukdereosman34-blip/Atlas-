package com.atlas.app.improvement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImprovementProposalTest {

    private ImprovementProposal createProposal() {
        return new ImprovementProposal(
                "Test proposal",
                "Test reason",
                "Test action",
                new ImprovementValidation(
                        "Run tests",
                        "All tests pass"
                )
        );
    }

    @Test
    public void proposalStartsAsProposed() {
        ImprovementProposal proposal = createProposal();

        assertEquals(
                ImprovementProposalStatus.PROPOSED,
                proposal.getStatus()
        );
    }

    @Test
    public void proposedProposalCanBeApproved() {
        ImprovementProposal proposal = createProposal();

        proposal.approve();

        assertEquals(
                ImprovementProposalStatus.APPROVED,
                proposal.getStatus()
        );
    }

    @Test
    public void proposedProposalCanBeRejected() {
        ImprovementProposal proposal = createProposal();

        proposal.reject();

        assertEquals(
                ImprovementProposalStatus.REJECTED,
                proposal.getStatus()
        );
    }

    @Test
    public void rejectedProposalCannotBeApproved() {
        ImprovementProposal proposal = createProposal();

        proposal.reject();
        proposal.approve();

        assertEquals(
                ImprovementProposalStatus.REJECTED,
                proposal.getStatus()
        );
    }

    @Test
    public void approvedProposalCannotBeRejected() {
        ImprovementProposal proposal = createProposal();

        proposal.approve();
        proposal.reject();

        assertEquals(
                ImprovementProposalStatus.APPROVED,
                proposal.getStatus()
        );
    }
}
