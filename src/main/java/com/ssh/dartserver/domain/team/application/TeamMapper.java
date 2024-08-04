package com.ssh.dartserver.domain.team.application;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.presentation.response.BlindDateTeamDetailResponse;
import com.ssh.dartserver.domain.team.presentation.response.RegionResponse;
import com.ssh.dartserver.domain.team.presentation.response.TeamResponse;
import com.ssh.dartserver.domain.team.presentation.response.BlindDateUserDetailResponse;
import com.ssh.dartserver.domain.team.presentation.response.BlindDateUserResponse;
import com.ssh.dartserver.domain.team.presentation.response.BlindDateTeamResponse;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.presentation.v1.response.ProfileQuestionResponse;
import com.ssh.dartserver.domain.user.presentation.v1.response.UserProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "name", source = "team.name.value")
    @Mapping(target = "isVisibleToSameUniversity", source = "team.isVisibleToSameUniversity")
    @Mapping(target = "teamRegions", source = "teamRegions")
    @Mapping(target = "teamUsers", source = "teamUsers")
    TeamResponse toTeamResponse(Team team, List<RegionResponse> teamRegions, List<UserProfileResponse> teamUsers);

    @Mapping(target = "id", source = "team.id")
    @Mapping(target = "name", source = "team.name.value")
    @Mapping(target = "averageAge", source = "averageAge")
    @Mapping(target = "regions", source = "teamRegions")
    @Mapping(target = "universityName", source = "universityName")
    @Mapping(target = "isCertifiedTeam", source = "isCertifiedTeam")
    @Mapping(target = "teamUsers", source = "teamUsers")
    BlindDateTeamResponse toBlindDateTeamResponse(Team team, Double averageAge, List<RegionResponse> teamRegions, String universityName, Boolean isCertifiedTeam, List<BlindDateUserResponse> teamUsers);

    @Mapping(target = "id", source = "team.id")
    @Mapping(target = "name", source = "team.name.value")
    @Mapping(target = "averageAge", source = "averageAge")
    @Mapping(target = "regions", source = "teamRegions")
    @Mapping(target = "universityName", source = "universityName")
    @Mapping(target = "isCertifiedTeam", source = "isCertifiedTeam")
    @Mapping(target = "teamUsers", source = "teamUsers")
    @Mapping(target = "isAlreadyProposalTeam", source = "isAlreadyProposalTeam")
    BlindDateTeamDetailResponse toBlindDateTeamDetailResponse(Team team, Double averageAge, List<RegionResponse> teamRegions, String universityName, Boolean isCertifiedTeam, List<BlindDateUserDetailResponse> teamUsers, Boolean isAlreadyProposalTeam);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "name", source = "nicknameOrName")
    @Mapping(target = "profileImageUrl", source = "user.personalInfo.profileImageUrl.value")
    @Mapping(target = "department", source = "user.university.department")
    @Mapping(target = "isCertifiedUser", source = "isCertifiedUser")
    @Mapping(target = "birthYear", source = "user.personalInfo.birthYear.value")
    @Mapping(target = "profileQuestionResponses", source = "profileQuestionResponses")
    BlindDateUserDetailResponse toBlindDateUserDetailResponse(User user, Boolean isCertifiedUser, String nicknameOrName, List<ProfileQuestionResponse> profileQuestionResponses);


    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "name", source = "nicknameOrName")
    @Mapping(target = "profileImageUrl", source = "user.personalInfo.profileImageUrl.value")
    @Mapping(target = "department", source = "user.university.department")
    BlindDateUserResponse toBlindDateUserResponse(User user, String nicknameOrName);
}
