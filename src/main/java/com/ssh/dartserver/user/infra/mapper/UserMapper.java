package com.ssh.dartserver.user.infra.mapper;

import com.ssh.dartserver.university.domain.University;
import com.ssh.dartserver.user.domain.User;
import com.ssh.dartserver.user.dto.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "universityName", source = "university.name")
    @Mapping(target = "universityId", source = "university.id")
    @Mapping(target = "department", source = "university.department")
    UserResponseDto toResponseDto(User user, University university);
}
