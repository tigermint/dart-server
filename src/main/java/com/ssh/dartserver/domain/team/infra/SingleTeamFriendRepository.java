package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.SingleTeamFriend;
import com.ssh.dartserver.domain.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SingleTeamFriendRepository extends JpaRepository<SingleTeamFriend, Long> {
    List<SingleTeamFriend> findAllByTeam(Team team);

    void deleteAllByTeamId(Long teamId);
}
