package com.ssh.dartserver.domain.proposal.infra;

import com.ssh.dartserver.domain.team.domain.Team;
import java.util.List;

public interface ProposalRepositoryCustom {
    void updateRequestingOrRequestedTeamsToNullIn(List<Team> teams);
}
