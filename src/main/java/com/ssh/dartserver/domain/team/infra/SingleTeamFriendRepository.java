package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.SingleTeamFriend;
import com.ssh.dartserver.domain.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SingleTeamFriendRepository extends JpaRepository<SingleTeamFriend, Long> {
    @Query("select distinct stf from SingleTeamFriend stf " +
            "join fetch stf.university u " +
            "where stf.team = :team")
    List<SingleTeamFriend> findAllByTeam(@Param("team") Team team);

    void deleteAllByTeamId(Long teamId);
}
