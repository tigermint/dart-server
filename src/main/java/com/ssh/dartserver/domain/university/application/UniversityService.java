package com.ssh.dartserver.domain.university.application;

import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.university.presentation.response.UniversityResponse;
import com.ssh.dartserver.domain.university.presentation.response.UniversitySearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UniversityService {
    private final UniversityRepository universityRepository;
    private final UniversityMapper universityMapper;

    @Transactional(readOnly = true)
    public List<UniversityResponse> search(UniversitySearchRequest request) {
        if (request.getDepartment() == null) {
            return searchBy(request.getName(), request.getSize());
        }
        return searchBy(request.getName(), request.getDepartment(), request.getSize());
    }

    private List<UniversityResponse> searchBy(String name, int size) {
        return universityRepository.findNamesStartWith(name, size)
            .stream()
            .map(universityMapper::toUniversityResponse)
            .collect(Collectors.toList());
    }

    private List<UniversityResponse> searchBy(String name, String department, int size) {
        Pageable pageRequest = createQueryLimit(size);
        return universityRepository.findDistinctByNameAndDepartmentStartsWith(name, department, pageRequest)
            .stream()
            .map(universityMapper::toUniversityResponse)
            .collect(Collectors.toList());
    }

    private static Pageable createQueryLimit(final int size) {
        return PageRequest.of(0, size, Sort.by("id").ascending());
    }
}
