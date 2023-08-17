package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRegionRepository extends JpaRepository<TeamRegion, Long> {
    List<TeamRegion> findAllByTeam(Team team);
    List<TeamRegion> findAllByTeamId(Long teamId);

    void deleteAllByTeamId(Long teamId);
}
