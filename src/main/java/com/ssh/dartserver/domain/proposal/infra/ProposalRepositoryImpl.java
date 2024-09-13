package com.ssh.dartserver.domain.proposal.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssh.dartserver.domain.proposal.domain.QProposal;
import com.ssh.dartserver.domain.team.domain.Team;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProposalRepositoryImpl implements ProposalRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Transactional
    @Override
    public void updateRequestingOrRequestedTeamsToNullIn(List<Team> teams) {
        QProposal proposal = QProposal.proposal;

        queryFactory.update(proposal)
                .set(proposal.requestingTeam, (Team) null)
                .where(proposal.requestingTeam.in(teams))
                .execute();

        queryFactory.update(proposal)
                .set(proposal.requestedTeam, (Team) null)
                .where(proposal.requestedTeam.in(teams))
                .execute();
    }
}
