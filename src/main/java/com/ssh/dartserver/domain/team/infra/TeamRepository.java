package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamUsersCombinationHash;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByTeamUsersCombinationHash(TeamUsersCombinationHash teamUsersCombinationHashValue);

    @Query("SELECT DISTINCT tu.team " +
            "FROM TeamUser tu " +
            "WHERE (tu.team.isVisibleToSameUniversity = true OR " +
            "(tu.team.isVisibleToSameUniversity = false AND tu.team.university.id != :myUniversityId)) " +
            "AND tu.user.personalInfo.gender != :myGender")
    Page<Team> findAllVisibleTeams(@Param("myUniversityId") Long myUniversityId,
                                   @Param("myGender") Gender myGender,
                                   Pageable pageable);

    @Query("SELECT DISTINCT tu.team " +
            "FROM TeamUser tu, TeamRegion tr " +
            "WHERE (tu.team.isVisibleToSameUniversity = true OR " +
            "(tu.team.isVisibleToSameUniversity = false AND tu.team.university.id != :myUniversityId)) " +
            "AND (tu.user.personalInfo.gender != :myGender) AND " +
            "(tu.team = tr.team AND tr.region.id = :regionId)")
    Page<Team> findAllVisibleTeamsByRegionId(@Param("myUniversityId") Long myUniversityId,
                                   @Param("myGender") Gender myGender,
                                   @Param("regionId") Long regionId,
                                   Pageable pageable);
}
