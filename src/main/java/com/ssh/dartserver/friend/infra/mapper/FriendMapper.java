package com.ssh.dartserver.friend.infra.mapper;

import com.ssh.dartserver.friend.dto.FriendResponseDto;
import com.ssh.dartserver.university.dto.UniversityResponseDto;
import com.ssh.dartserver.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendMapper {

    @Mapping(target = "userId", source = "friendUserInfo.id")
    @Mapping(target = "admissionYear", source = "friendUserInfo.personalInfo.admissionYear.value")
    @Mapping(target = "name", source = "friendUserInfo.personalInfo.name.value")
    @Mapping(target = "university", source = "university")
    FriendResponseDto toFriendResponseDto(User friendUserInfo, UniversityResponseDto university);
}
