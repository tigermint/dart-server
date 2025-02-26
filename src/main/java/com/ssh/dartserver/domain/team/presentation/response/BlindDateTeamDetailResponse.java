package com.ssh.dartserver.domain.team.presentation.response;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BlindDateTeamDetailResponse {
    private Long id;
    private String name;
    private Double averageAge;
    private List<RegionResponse> regions;
    private String universityName;
    private Boolean isCertifiedTeam;
    private List<BlindDateUserDetailResponse> teamUsers;
    private Boolean isAlreadyProposalTeam;
}
