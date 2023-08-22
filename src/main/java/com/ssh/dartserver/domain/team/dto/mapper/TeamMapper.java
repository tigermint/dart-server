package com.ssh.dartserver.domain.team.dto.mapper;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.dto.RegionResponse;
import com.ssh.dartserver.domain.team.dto.TeamResponse;
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
}
