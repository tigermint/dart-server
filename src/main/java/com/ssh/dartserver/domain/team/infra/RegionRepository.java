package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {

}
