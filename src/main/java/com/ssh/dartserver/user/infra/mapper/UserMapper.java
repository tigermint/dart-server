package com.ssh.dartserver.user.infra.mapper;

import com.ssh.dartserver.university.dto.UniversityResponseDto;
import com.ssh.dartserver.user.dto.UserNextVoteResponseDto;
import com.ssh.dartserver.user.domain.User;
import com.ssh.dartserver.user.dto.UserResponseDto;
import com.ssh.dartserver.user.dto.UserWithUniversityResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userResponseDto", source = "userResponseDto")
    @Mapping(target = "universityResponseDto", source = "universityResponseDto")
    UserWithUniversityResponseDto toUserWithUniversityResponseDto(UserResponseDto userResponseDto, UniversityResponseDto universityResponseDto);

    @Mapping(target = "name", source = "user.personalInfo.name.value")
    @Mapping(target = "phone", source = "user.personalInfo.phone.value")
    @Mapping(target = "gender", source = "user.personalInfo.gender.value")
    @Mapping(target = "admissionYear", source = "user.personalInfo.admissionYear.value")
    @Mapping(target = "birthYear", source = "user.personalInfo.birthYear.value")
    @Mapping(target = "recommendationCode", source = "user.recommendationCode.value")
    UserResponseDto toUserResponseDto(User user);

    @Mapping(target = "nextVoteAvailableDateTime", source = "user.nextVoteAvailableDateTime.value")
    UserNextVoteResponseDto toUserNextVoteResponseDto(User user);
}
