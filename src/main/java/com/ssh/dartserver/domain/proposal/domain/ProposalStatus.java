package com.ssh.dartserver.domain.proposal.domain;
public enum ProposalStatus {
    PROPOSAL_IN_PROGRESS, PROPOSAL_SUCCESS, PROPOSAL_FAILED;

    public boolean isSuccess() {
        return this == PROPOSAL_SUCCESS;
    }

    public boolean isFailed() {
        return this == PROPOSAL_FAILED;
    }
}
