package com.ssh.dartserver.domain.proposal.infra;

import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.domain.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    Optional<Proposal> findByRequestingTeamIdAndRequestedTeamId(Long requestingTeamId, Long requestedTeamId);

    @Query("select p from Proposal p join fetch p.requestingTeam rt " +
            "where concat('-', rt.teamUsersCombinationHash.value, '-') like :userIdPattern " +
            "and p.proposalStatus = :proposalStatus ")
    List<Proposal> findAllRequestingTeamByUserIdPatternAndProposalStatus(@Param("userIdPattern") String userIdPattern, @Param("proposalStatus") ProposalStatus proposalStatus);

    @Query("select p from Proposal p join fetch p.requestedTeam rt " +
            "where concat('-', rt.teamUsersCombinationHash.value, '-') like :userIdPattern " +
            "and p.proposalStatus = :proposalStatus ")
    List<Proposal> findAllRequestedTeamByUserIdPatternAndProposalStatus(@Param("userIdPattern") String userIdPattern, @Param("proposalStatus") ProposalStatus proposalStatus);


}


