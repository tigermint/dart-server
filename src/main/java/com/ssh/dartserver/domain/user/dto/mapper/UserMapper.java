package com.ssh.dartserver.domain.user.dto.mapper;

import com.ssh.dartserver.domain.university.dto.UniversityResponse;
import com.ssh.dartserver.domain.user.dto.*;
import com.ssh.dartserver.domain.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userResponse", source = "userResponse")
    @Mapping(target = "universityResponse", source = "universityResponse")
    @Mapping(target = "profileQuestionResponses", source = "profileQuestionResponse")
    UserProfileResponse toUserProfileResponse(UserResponse userResponse, UniversityResponse universityResponse, List<ProfileQuestionResponse> profileQuestionResponse);

    @Mapping(target = "userResponse", source = "userResponse")
    @Mapping(target = "universityResponse", source = "universityResponse")
    UserWithUniversityResponse toUserWithUniversityResponse(UserResponse userResponse, UniversityResponse universityResponse);

    @Mapping(target = "name", source = "user.personalInfo.name.value")
    @Mapping(target = "nickname", source = "user.personalInfo.nickname.value")
    @Mapping(target = "phone", source = "user.personalInfo.phone.value")
    @Mapping(target = "gender", source = "user.personalInfo.gender.value")
    @Mapping(target = "admissionYear", source = "user.personalInfo.admissionYear.value")
    @Mapping(target = "birthYear", source = "user.personalInfo.birthYear.value")
    @Mapping(target = "profileImageUrl", source = "user.personalInfo.profileImageUrl.value")
    @Mapping(target = "recommendationCode", source = "user.recommendationCode.value")
    @Mapping(target = "point", source = "user.point.value")
    @Mapping(target = "studentIdCardImageUrl", source = "user.studentVerificationInfo.studentIdCardImageUrl.value")
    @Mapping(target = "studentIdCardVerificationStatus", source = "user.studentVerificationInfo.studentIdCardVerificationStatus")
    UserResponse toUserResponse(User user);

    @Mapping(target = "nextVoteAvailableDateTime", source = "nextVoteAvailableDateTime")
    UserNextVoteResponse toUserNextVoteResponseDto(LocalDateTime nextVoteAvailableDateTime);
}
