package com.ssh.dartserver.domain.university.service;

import com.ssh.dartserver.domain.university.dto.UniversityResponse;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.university.dto.mapper.UniversityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UniversityService {
    private final UniversityRepository universityRepository;
    private final UniversityMapper universityMapper;

    public List<UniversityResponse> list() {
        return universityRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(universityMapper::toUniversityResponse)
                .collect(Collectors.toList());
    }
}
