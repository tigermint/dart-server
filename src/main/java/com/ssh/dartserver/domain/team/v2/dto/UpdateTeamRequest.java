package com.ssh.dartserver.domain.team.v2.dto;

import java.util.List;

public record UpdateTeamRequest(
        String name,
        String description,
        boolean isVisibleToSameUniversity,
        List<Long> regionIds,
        List<String> imageUrls
) {
}
