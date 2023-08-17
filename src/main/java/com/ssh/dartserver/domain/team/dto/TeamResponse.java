package com.ssh.dartserver.domain.team.dto;

import com.ssh.dartserver.domain.user.dto.UserWithUniversityResponse;
import lombok.Data;

import java.util.List;

@Data
public class TeamResponse {
    private Long teamId;
    private String name;
    private Boolean isVisibleToSameUniversity;
    private List<RegionResponse> teamRegions;
    private List<UserWithUniversityResponse> teamUsers;
}
