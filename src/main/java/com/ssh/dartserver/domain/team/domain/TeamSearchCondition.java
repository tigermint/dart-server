package com.ssh.dartserver.domain.team.domain;

import lombok.Data;
@Data
public class TeamSearchCondition {
    private OrderMethod order;
    private Long regionId;
    private Boolean verifiedStudent;
    private Boolean hasProfileImage;
}
