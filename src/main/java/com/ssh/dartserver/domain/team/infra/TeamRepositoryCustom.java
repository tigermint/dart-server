package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface TeamRepositoryCustom {
    Page<Team> findAllVisibleTeams(@Param("myUniversityId") Long myUniversityId,
                                   @Param("myGender") Gender myGender,
                                   Pageable pageable);
    Page<Team> findAllVisibleTeamsByRegionId(@Param("myUniversityId") Long myUniversityId,
                                             @Param("myGender") Gender myGender,
                                             @Param("regionId") Long regionId,
                                             Pageable pageable);
}
