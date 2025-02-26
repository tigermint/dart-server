package com.ssh.dartserver.domain.team.presentation.request;

import com.ssh.dartserver.domain.team.domain.OrderMethod;
import lombok.Data;

@Deprecated(since = "20241015", forRemoval = true)  // TODO #217에서 반드시 제거할 것
@Data
public class TeamSearchCondition {
    private OrderMethod order;
    private Long regionId;
    private Boolean verifiedStudent;
    private Boolean hasProfileImage;
}
