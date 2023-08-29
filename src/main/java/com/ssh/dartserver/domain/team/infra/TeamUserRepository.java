package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {
    List<TeamUser> findAllByTeamId(Long teamId);
    List<TeamUser> findAllByUser(User user);
    List<TeamUser> findAllByTeam(Team team);
    void deleteAllByTeamId(Long teamId);
}
