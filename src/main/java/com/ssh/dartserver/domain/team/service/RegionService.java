package com.ssh.dartserver.domain.team.service;

import com.ssh.dartserver.domain.team.dto.RegionResponse;
import com.ssh.dartserver.domain.team.dto.mapper.RegionMapper;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {
    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;
    public List<RegionResponse> listRegion() {
        return regionRepository.findAll().stream()
                .map(regionMapper::toRegionResponse)
                .collect(Collectors.toList());
    }
}
