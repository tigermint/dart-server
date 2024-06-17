package com.ssh.dartserver.domain.university.service;

import com.ssh.dartserver.domain.university.dto.UniversityResponse;
import com.ssh.dartserver.domain.university.dto.UniversitySearchRequest;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.university.dto.mapper.UniversityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UniversityService {
    private final UniversityRepository universityRepository;
    private final UniversityMapper universityMapper;

    public List<UniversityResponse> search(UniversitySearchRequest request) {
        if (request.getDepartment() == null) {
            return searchBy(request.getName(), request.getSize());
        } else {
            return searchBy(request.getName(), request.getDepartment(), request.getSize());
        }
    }

    public List<UniversityResponse> searchBy(String name, int size) {
        return universityRepository.findTop0ByNameStartsWith(name, size).stream()
            .map(UniversityResponse::new)
            .collect(Collectors.toList());
    }

    public List<UniversityResponse> searchBy(String name, String department, int size) {
        // TODO Size 활용하기
        return universityRepository.findDistinctTop10ByNameAndDepartmentStartsWith(name, department).stream()
            .map(universityMapper::toUniversityResponse)
            .collect(Collectors.toList());
    }
}
