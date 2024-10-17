package com.ssh.dartserver.domain.team.v2;

import com.ssh.dartserver.domain.team.domain.TeamImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamImageRepository extends JpaRepository<TeamImage, Long> {
}