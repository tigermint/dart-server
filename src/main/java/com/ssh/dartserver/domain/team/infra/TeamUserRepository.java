package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {
    List<TeamUser> findAllByUser(User user);

    List<TeamUser> findAllByTeam(Team team);

    @Query("select distinct tu from TeamUser tu " +
            "join fetch tu.team t " +
            "join fetch tu.user u " +
            "join fetch u.university " +
            "join fetch u.profileQuestions pqs " +
            "join fetch pqs.values pq " +
            "join fetch pq.question q " +
            "where t.id = :teamId")
    List<TeamUser> findAllByTeamId(@Param("teamId") Long teamId);

    List<TeamUser> findAllByTeamIn(List<Team> teams);
    void deleteAllByTeamId(Long teamId);
}
