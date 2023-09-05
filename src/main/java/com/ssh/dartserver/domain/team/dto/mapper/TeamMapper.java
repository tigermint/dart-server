package com.ssh.dartserver.domain.team.dto.mapper;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.dto.BlindDateTeamDetailResponse;
import com.ssh.dartserver.domain.team.dto.RegionResponse;
import com.ssh.dartserver.domain.team.dto.TeamResponse;
import com.ssh.dartserver.domain.team.dto.BlindDateUserDetailResponse;
import com.ssh.dartserver.domain.team.dto.BlindDateUserResponse;
import com.ssh.dartserver.domain.team.dto.BlindDateTeamResponse;
import com.ssh.dartserver.domain.user.dto.UserProfileResponse;
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
    @Mapping(target = "averageBirthYear", source = "averageBirthYear")
    @Mapping(target = "regions", source = "teamRegions")
    @Mapping(target = "universityName", source = "universityName")
    @Mapping(target = "isCertifiedTeam", source = "isCertifiedTeam")
    @Mapping(target = "teamUsers", source = "teamUsers")
    BlindDateTeamResponse toBlindDateTeamResponse(Team team, double averageBirthYear, List<RegionResponse> teamRegions, String universityName, boolean isCertifiedTeam, List<BlindDateUserResponse> teamUsers);

    @Mapping(target = "id", source = "team.id")
    @Mapping(target = "name", source = "team.name.value")
    @Mapping(target = "averageBirthYear", source = "averageBirthYear")
    @Mapping(target = "regions", source = "teamRegions")
    @Mapping(target = "universityName", source = "universityName")
    @Mapping(target = "isCertifiedTeam", source = "isCertifiedTeam")
    @Mapping(target = "teamUsers", source = "teamUsers")
    BlindDateTeamDetailResponse toBlindDateTeamDetailResponse(Team team, double averageBirthYear, List<RegionResponse> teamRegions, String universityName, boolean isCertifiedTeam, List<BlindDateUserDetailResponse> teamUsers);
}
