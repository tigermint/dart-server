package com.ssh.dartserver.university.service;

import com.ssh.dartserver.university.dto.UniversityResponse;
import com.ssh.dartserver.university.dto.mapper.UniversityMapper;
import com.ssh.dartserver.university.infra.UniversityRepository;
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
                .map(universityMapper::toUniversityResponseDto)
                .collect(Collectors.toList());
    }
}
