package com.ssh.dartserver.domain.university.dto.mapper;

import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.dto.UniversityResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UniversityMapper {
    UniversityResponse toUniversityResponseDto(University university);
}
