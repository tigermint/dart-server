package com.ssh.dartserver.university.dto.mapper;

import com.ssh.dartserver.university.domain.University;
import com.ssh.dartserver.university.dto.UniversityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UniversityMapper {
    UniversityMapper INSTANCE = Mappers.getMapper(UniversityMapper.class);

    University toEntity(UniversityResponse universityDto);
    UniversityResponse toUniversityResponseDto(University university);
}
