package com.ssh.dartserver.domain.team.v2;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlindDateTeamRepository {

    Page<Team> findAll(User user, Pageable pageable);

}
