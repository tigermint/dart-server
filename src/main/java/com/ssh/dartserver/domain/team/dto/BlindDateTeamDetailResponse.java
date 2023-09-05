package com.ssh.dartserver.domain.team.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BlindDateTeamDetailResponse {
    private long id;
    private String name;
    private double averageBirthYear;
    private List<RegionResponse> regions;
    private String universityName;
    private Boolean isCertifiedTeam;
    private List<BlindDateUserDetailResponse> teamUsers;
}
