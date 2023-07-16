package com.ssh.dartserver.user.infra.mapper;

import com.ssh.dartserver.university.dto.UniversityResponse;
import com.ssh.dartserver.user.dto.UserNextVoteResponse;
import com.ssh.dartserver.user.domain.User;
import com.ssh.dartserver.user.dto.UserResponse;
import com.ssh.dartserver.user.dto.UserWithUniversityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userResponseDto", source = "userResponseDto")
    @Mapping(target = "universityResponseDto", source = "universityResponseDto")
    UserWithUniversityResponse toUserWithUniversityResponseDto(UserResponse userResponseDto, UniversityResponse universityResponseDto);

    @Mapping(target = "name", source = "user.personalInfo.name.value")
    @Mapping(target = "phone", source = "user.personalInfo.phone.value")
    @Mapping(target = "gender", source = "user.personalInfo.gender.value")
    @Mapping(target = "admissionYear", source = "user.personalInfo.admissionYear.value")
    @Mapping(target = "birthYear", source = "user.personalInfo.birthYear.value")
    @Mapping(target = "recommendationCode", source = "user.recommendationCode.value")
    UserResponse toUserResponseDto(User user);

    @Mapping(target = "nextVoteAvailableDateTime", source = "user.nextVoteAvailableDateTime.value")
    UserNextVoteResponse toUserNextVoteResponseDto(User user);
}
