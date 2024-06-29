package com.ssh.dartserver.domain.university.service;

import com.ssh.dartserver.domain.university.dto.UniversityResponse;
import com.ssh.dartserver.domain.university.dto.UniversitySearchRequest;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.university.dto.mapper.UniversityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UniversityService {
    private final UniversityRepository universityRepository;
    private final UniversityMapper universityMapper;

    @Transactional(readOnly = true)
    public List<UniversityResponse> search(UniversitySearchRequest request) {
        if (request.getDepartment() == null) {
            return searchBy(request.getName());
        } else {
            return searchBy(request.getName(), request.getDepartment());
        }
    }

    public List<UniversityResponse> searchBy(String name) {
        return universityRepository.findTop20ByNameStartsWith(name).stream()
            .map(UniversityResponse::new)
            .collect(Collectors.toList());
    }

    public List<UniversityResponse> searchBy(String name, String department) {
        return universityRepository.findDistinctTop20ByNameAndDepartmentStartsWith(name, department).stream()
            .map(universityMapper::toUniversityResponse)
            .collect(Collectors.toList());
    }
}
