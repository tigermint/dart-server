package com.ssh.dartserver.domain.team.dto;

import com.ssh.dartserver.domain.user.dto.ProfileQuestionResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BlindDateUserDetailResponse {
    private Long id;
    private String name;
    private String profileImageUrl;
    private String department;
    private Boolean isCertifiedUser;
    private Integer birthYear;
    private List<ProfileQuestionResponse> profileQuestionResponses;
}
