package com.ssh.dartserver.university.infra.mapper;

import com.ssh.dartserver.university.domain.University;
import com.ssh.dartserver.university.dto.UniversityResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UniversityMapper {
    UniversityMapper INSTANCE = Mappers.getMapper(UniversityMapper.class);

    University toEntity(UniversityResponseDto universityDto);
    UniversityResponseDto toUniversityResponseDto(University university);
}
