package com.ssh.dartserver.domain.team.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BlindDateTeamResponse {
    private long id;
    private String name;
    private double averageBirthYear;
    private List<RegionResponse> regions;
    private String universityName;
    private Boolean isCertifiedTeam;
    private List<BlindDateUserResponse> teamUsers;
}
