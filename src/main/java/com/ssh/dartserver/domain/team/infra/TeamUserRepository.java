package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {
    List<TeamUser> findAllByUser(User user);

    @Query("select distinct tu from TeamUser tu " +
            "join fetch tu.team t " +
            "join fetch tu.user u " +
            "join fetch u.university uni " +
            "where t.id = :teamId")
    List<TeamUser> findAllByTeamId(@Param("teamId") Long teamId);

    @Query("select distinct tu from TeamUser tu " +
            "join fetch tu.team t " +
            "join fetch tu.user u " +
            "join fetch u.university uni " +
            "where t in :teams")
    List<TeamUser> findAllByTeamIn(@Param("teams") List<Team> teams);

    void deleteAllByTeamId(Long teamId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TeamUser tu WHERE tu.team IN :teams")
    void deleteAllByTeamsInBatch(@Param("teams") List<Team> teams);
}
