package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamSearchCondition;
import com.ssh.dartserver.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamRepositoryCustom {
    Page<Team> findAllVisibleTeam(User user, TeamSearchCondition condition, Pageable pageable);
}
