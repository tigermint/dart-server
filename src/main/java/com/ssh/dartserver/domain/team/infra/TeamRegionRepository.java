package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRegionRepository extends JpaRepository<TeamRegion, Long> {
    List<TeamRegion> findAllByTeam(Team team);
    @Query("select distinct tr from TeamRegion tr " +
            "join fetch tr.team t " +
            "join fetch tr.region " +
            "where t.id = :teamId")
    List<TeamRegion> findAllByTeamId(@Param("teamId") Long teamId);
    void deleteAllByTeamId(Long teamId);
}
