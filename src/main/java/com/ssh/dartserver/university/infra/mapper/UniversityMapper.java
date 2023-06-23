package com.ssh.dartserver.university.infra.mapper;

import com.ssh.dartserver.university.domain.University;
import com.ssh.dartserver.university.dto.UniversityDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UniversityMapper {
    UniversityMapper INSTANCE = Mappers.getMapper(UniversityMapper.class);

    University fromDto(UniversityDto universityDto);
    UniversityDto toDto(University university);
}
