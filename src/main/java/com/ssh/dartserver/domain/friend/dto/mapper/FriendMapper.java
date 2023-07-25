package com.ssh.dartserver.domain.friend.dto.mapper;

import com.ssh.dartserver.domain.friend.dto.FriendResponse;
import com.ssh.dartserver.domain.university.dto.UniversityResponse;
import com.ssh.dartserver.domain.user.domain.User;
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
