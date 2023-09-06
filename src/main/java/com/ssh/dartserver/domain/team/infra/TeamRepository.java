package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamUsersCombinationHash;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {
    Optional<Team> findByTeamUsersCombinationHash(TeamUsersCombinationHash teamUsersCombinationHashValue);
}
