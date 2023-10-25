package com.ssh.dartserver.domain.team.dto;

import lombok.Data;
@Data
public class TeamSearchCondition {
    private OrderMethod order;
    private Long regionId;
    private Boolean verifiedStudent;
    private Boolean hasProfileImage;
}
