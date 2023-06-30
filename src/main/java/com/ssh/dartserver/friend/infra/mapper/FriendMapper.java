package com.ssh.dartserver.friend.infra.mapper;

import com.ssh.dartserver.friend.dto.FriendResponseDto;
import com.ssh.dartserver.friend.dto.RequiredFriendResponseDto;
import com.ssh.dartserver.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendMapper {

    @Mapping(target = "friendUserId", source = "friendUserInfo.id")
    @Mapping(target = "friendName", source = "friendUserInfo.name")
    @Mapping(target = "universityId", source = "friendUserInfo.university.id")
    @Mapping(target = "admissionNum", source = "friendUserInfo.admissionNum")
    FriendResponseDto toFriendResponseDto(User friendUserInfo);

    @Mapping(target = "userId", source = "requiredFriendUserInfo.id")
    @Mapping(target = "universityId", source = "requiredFriendUserInfo.university.id")
    @Mapping(target = "admissionNum", source = "requiredFriendUserInfo.admissionNum")
    @Mapping(target = "name", source = "requiredFriendUserInfo.name")
    RequiredFriendResponseDto toRequiredFriendResponseDto(User requiredFriendUserInfo);
}
