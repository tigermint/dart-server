package com.ssh.dartserver.domain.team.application;

import com.ssh.dartserver.domain.team.presentation.response.RegionResponse;
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
