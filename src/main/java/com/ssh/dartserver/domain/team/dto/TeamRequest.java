package com.ssh.dartserver.domain.team.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ValidSingleTeamRequest
public class TeamRequest {
    @NotBlank(message = "팀 이름은 blank가 될 수 없습니다.")
    private String name;

    @NotNull(message = "우리 학교만 보기는 null이 될 수 없습니다.")
    private Boolean isVisibleToSameUniversity;

    @NotNull(message = "지역은 null이 될 수 없습니다.")
    @Size(min = 1, message = "지역은 최소 1개 이상이어야 합니다.")
    private List<Long> regionIds;

    @NotNull(message = "팀원은 null이 될 수 없습니다.")
    private List<Long> userIds;

    private List<SingleTeamFriendDto> singleTeamFriends;

    @Data
    public static class SingleTeamFriendDto{
        @NotBlank(message = "친구의 닉네임은 blank가 될 수 없습니다.")
        private String nickname;

        @NotNull(message = "친구의 생년은 null이 될 수 없습니다.")
        private int birthYear;

        @NotNull(message = "친구의 학교 정보는 null이 될 수 없습니다.")
        private Long universityId;

        private String profileImageUrl;
    }
}
