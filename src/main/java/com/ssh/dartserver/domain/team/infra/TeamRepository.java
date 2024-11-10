package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.vo.TeamUsersCombinationHash;
import com.ssh.dartserver.domain.team.v2.BlindDateTeamRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom, BlindDateTeamRepository {
    Optional<Team> findByTeamUsersCombinationHash(TeamUsersCombinationHash teamUsersCombinationHashValue);

    @Query("select t from Team t where concat('-', t.teamUsersCombinationHash.value, '-') like :userIdPattern " )
    List<Team> findAllTeamByUserIdPattern(@Param("userIdPattern") String userIdPattern);

    @Modifying
    @Transactional
    @Query("update Team t " +
            "set t.viewCount.value = t.viewCount.value + :viewCountIncrement " +
            "where t in :teams")
    void increaseAllTeamViewCount(@Param("teams") List<Team> teams, @Param("viewCountIncrement") int viewCountIncrement);

    @Query("select (count(t) > 0) from Team t where t.leader.id = ?1")
    boolean existsByLeader_Id(@NonNull Long id);

    Optional<Team> findByLeader_IdOrTeamUsers_User_Id(Long id, Long id1);

    @Query("SELECT DISTINCT t FROM Team t " +
            "LEFT JOIN FETCH t.teamUsers tu " +
            "LEFT JOIN FETCH tu.user " +
            "LEFT JOIN FETCH t.leader " +
            "WHERE t.leader.id = :userId " +
            "OR EXISTS (SELECT 1 FROM TeamUser tui WHERE tui.team = t AND tui.user.id = :userId)")
    Optional<Team> findByLeader_IdOrTeamUsers_User_Id(
            @Param("userId") Long userId
    );

}
