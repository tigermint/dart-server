package com.ssh.dartserver.domain.team.v2.dto;

import java.util.List;

public record BlindDateTeamSearchCondition(
        int page,
        int size,
        List<String> sort
) {
}
