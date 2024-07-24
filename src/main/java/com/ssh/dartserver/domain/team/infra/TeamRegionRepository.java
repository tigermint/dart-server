package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeamRegionRepository extends JpaRepository<TeamRegion, Long> {

    @Query("select distinct tr from TeamRegion tr " +
            "join fetch tr.team t " +
            "join fetch tr.region " +
            "where t.id = :teamId")
    List<TeamRegion> findAllByTeamId(@Param("teamId") Long teamId);

    @Query("select distinct tr from TeamRegion tr " +
            "join fetch tr.team t " +
            "join fetch tr.region " +
            "where t in :teams")
    List<TeamRegion> findAllByTeamIn(@Param("teams") List<Team> teams);

    void deleteAllByTeamId(Long teamId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TeamRegion tr WHERE tr.team IN :teams")
    void deleteAllByTeamsInBatch(List<Team> teams);
}
