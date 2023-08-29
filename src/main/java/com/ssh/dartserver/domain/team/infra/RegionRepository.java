package com.ssh.dartserver.domain.team.infra;

import com.ssh.dartserver.domain.team.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findAllByIdIn(List<Long> regionIds);
}
