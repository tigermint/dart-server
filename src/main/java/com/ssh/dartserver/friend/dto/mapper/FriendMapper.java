package com.ssh.dartserver.friend.dto.mapper;

import com.ssh.dartserver.friend.dto.FriendResponse;
import com.ssh.dartserver.university.dto.UniversityResponse;
import com.ssh.dartserver.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendMapper {

    @Mapping(target = "userId", source = "friendUserInfo.id")
    @Mapping(target = "admissionYear", source = "friendUserInfo.personalInfo.admissionYear.value")
    @Mapping(target = "name", source = "friendUserInfo.personalInfo.name.value")
    @Mapping(target = "university", source = "university")
    FriendResponse toFriendResponseDto(User friendUserInfo, UniversityResponse university);
}
