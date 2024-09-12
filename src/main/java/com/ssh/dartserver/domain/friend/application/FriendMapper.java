package com.ssh.dartserver.domain.friend.application;

import com.ssh.dartserver.domain.friend.presentation.response.FriendResponse;
import com.ssh.dartserver.domain.university.presentation.response.UniversityResponse;
import com.ssh.dartserver.domain.user.presentation.v1.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendMapper {

    @Mapping(target = "userResponseDto", source = "friend")
    @Mapping(target = "universityResponseDto", source = "university")
    FriendResponse toFriendResponseDto(UserResponse friend, UniversityResponse university);
}
