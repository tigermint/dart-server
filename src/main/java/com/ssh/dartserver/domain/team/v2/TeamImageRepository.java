package com.ssh.dartserver.domain.team.v2;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamImageRepository extends JpaRepository<TeamImage, Long> {
    List<TeamImage> findAllByTeam(Team team);
}
