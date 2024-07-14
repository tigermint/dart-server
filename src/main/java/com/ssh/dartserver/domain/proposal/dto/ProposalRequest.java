package com.ssh.dartserver.domain.proposal.dto;

import lombok.Data;

public class ProposalRequest {
    private ProposalRequest() {
        throw new IllegalStateException("Utility class");
    }

    @Data
    public static class Create {
        private Long requestingTeamId;
        private Long requestedTeamId;
    }

    @Data
    public static class Update{
        private String proposalStatus;  // TODO Enum?
    }

}
