package com.ssh.dartserver.domain.proposal.infra;

import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.domain.ProposalStatus;
import com.ssh.dartserver.domain.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    Optional<Proposal> findByRequestingTeamIdAndRequestedTeamId(Long requestingTeamId, Long requestedTeamId);

    List<Proposal> findAllByRequestingTeamIdOrRequestedTeamId(Long requestingTeamId, Long requestedTeamId);

    List<Proposal> findAllByRequestingTeamOrRequestedTeam(Team team1, Team team2);

    @Query("select p from Proposal p join fetch p.requestingTeam rt " +
            "where concat('-', rt.teamUsersCombinationHash.value, '-') like :userIdPattern " +
            "and p.proposalStatus = :proposalStatus ")
    List<Proposal> findAllRequestingProposalByUserIdPatternAndProposalStatus(@Param("userIdPattern") String userIdPattern, @Param("proposalStatus") ProposalStatus proposalStatus);

    @Query("select p from Proposal p join fetch p.requestedTeam rt " +
            "where concat('-', rt.teamUsersCombinationHash.value, '-') like :userIdPattern " +
            "and p.proposalStatus = :proposalStatus ")
    List<Proposal> findAllRequestedProposalByUserIdPatternAndProposalStatus(@Param("userIdPattern") String userIdPattern, @Param("proposalStatus") ProposalStatus proposalStatus);

    void deleteAllByRequestingTeamOrRequestedTeam(Team team1, Team team2);
}


