package com.ssh.dartserver.domain.proposal.application;

import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.presentation.response.ProposalResponse;
import com.ssh.dartserver.domain.team.domain.Region;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProposalMapper {
    @Mapping(target = "proposalId", source = "proposal.id")
    @Mapping(target = "createdTime", source = "proposal.createdTime")
    @Mapping(target = "requestedTeam", source = "requestedTeam")
    @Mapping(target = "requestingTeam", source = "requestingTeam")
    ProposalResponse.ListDto toListDto(Proposal proposal, ProposalResponse.ListDto.TeamDto requestingTeam, ProposalResponse.ListDto.TeamDto requestedTeam);

    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "name", source = "team.name.value")
    @Mapping(target = "averageAge", source = "averageAge")
    @Mapping(target = "users", source = "users")
    @Mapping(target = "regions", source = "regions")
    ProposalResponse.ListDto.TeamDto toListTeamDto(Team team, Double averageAge, List<ProposalResponse.ListDto.UserDto> users, List<ProposalResponse.ListDto.RegionDto> regions);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "nickname", source = "user.personalInfo.nickname.value")
    @Mapping(target = "birthYear", source = "user.personalInfo.birthYear.value")
    @Mapping(target = "studentIdCardVerificationStatus", source = "user.studentVerificationInfo.studentIdCardVerificationStatus")
    @Mapping(target = "profileImageUrl", source = "user.personalInfo.profileImageUrl.value")
    @Mapping(target = "university", source = "university")
    ProposalResponse.ListDto.UserDto toListUserDto(User user, ProposalResponse.ListDto.UniversityDto university);

    @Mapping(target = "regionId", source = "region.id")
    @Mapping(target = "name", source = "region.name")
    ProposalResponse.ListDto.RegionDto toListRegionDto(Region region);

    @Mapping(target = "universityId", source = "university.id")
    @Mapping(target = "name", source = "university.name")
    @Mapping(target = "department", source = "university.department")
    ProposalResponse.ListDto.UniversityDto toListUniversityDto(University university);

    @Mapping(target = "proposalId", source = "proposal.id")
    @Mapping(target = "proposalStatus", source = "proposal.proposalStatus")
    @Mapping(target = "requestingTeamId", source = "proposal.requestingTeam.id")
    @Mapping(target = "requestedTeamId", source = "proposal.requestedTeam.id")
    ProposalResponse.UpdateDto toUpdateDto(Proposal proposal);

}
