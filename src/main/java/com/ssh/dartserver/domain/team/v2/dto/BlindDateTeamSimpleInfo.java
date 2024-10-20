package com.ssh.dartserver.domain.team.v2.dto;

import com.ssh.dartserver.domain.team.presentation.response.RegionResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record BlindDateTeamSimpleInfo(
        long id,
        long leaderId,

        // user info
        int age,
        boolean isCertified,
        String universityName,
        String departmentName,

        // team info
        String name,
        String description,
        boolean isVisibleToSameUniversity,
        List<RegionResponse> regions,
        List<String> imageUrls,
        boolean isAlreadyProposalTeam
) {
}
