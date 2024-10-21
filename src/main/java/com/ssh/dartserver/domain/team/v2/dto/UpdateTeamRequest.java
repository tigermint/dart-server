package com.ssh.dartserver.domain.team.v2.dto;

import java.util.List;

public record UpdateTeamRequest(
        long teamId,
        String name,
        String description,
        boolean isVisibleToSameUniversity,
        List<Long> regionIds,
        List<String> imageUrls
) {
}
