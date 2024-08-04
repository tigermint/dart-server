package com.ssh.dartserver.domain.university.application;

import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.presentation.response.UniversityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UniversityMapper {
    UniversityMapper INSTANCE = Mappers.getMapper(UniversityMapper.class);

    UniversityResponse toUniversityResponse(University university);
    UniversityResponse toUniversityResponse(String name);
}
