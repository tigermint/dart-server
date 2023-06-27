package com.ssh.dartserver.user.infra.mapper;

import com.ssh.dartserver.user.domain.User;
import com.ssh.dartserver.user.dto.UserResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toResponseDto(User user);
}
