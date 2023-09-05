package com.ssh.dartserver.domain.team.dto;

import com.ssh.dartserver.domain.user.dto.ProfileQuestionResponse;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlindDateUserDetailResponse {
    private long id;
    private String name;
    private String profileImageUrl;
    private String department;
    private Boolean isCertifiedUser;
    private int birthYear;
    private List<ProfileQuestionResponse> profileQuestionResponses;
}
