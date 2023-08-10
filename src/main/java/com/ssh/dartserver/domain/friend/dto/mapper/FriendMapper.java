package com.ssh.dartserver.domain.friend.dto.mapper;

import com.ssh.dartserver.domain.friend.dto.FriendResponse;
import com.ssh.dartserver.domain.university.dto.UniversityResponse;
import com.ssh.dartserver.domain.user.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendMapper {

    @Mapping(target = "userResponseDto", source = "friend")
    @Mapping(target = "universityResponseDto", source = "university")
    FriendResponse toFriendResponseDto(UserResponse friend, UniversityResponse university);
}
