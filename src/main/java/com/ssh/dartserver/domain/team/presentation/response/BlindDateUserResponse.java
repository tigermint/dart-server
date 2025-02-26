package com.ssh.dartserver.domain.team.presentation.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlindDateUserResponse {
    private Long id;
    private String name;
    private String profileImageUrl;
    private String department;
}
