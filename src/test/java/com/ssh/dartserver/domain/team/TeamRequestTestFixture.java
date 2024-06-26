package com.ssh.dartserver.domain.team;

import com.ssh.dartserver.domain.team.dto.TeamRequest;

import java.util.List;

public class TeamRequestTestFixture {
    public static TeamRequest getTeamRequest() {
        final TeamRequest request = new TeamRequest();
        final int randomNum = (int) (Math.random() * 900000) + 100000;
        request.setName("팀" + randomNum);
        request.setIsVisibleToSameUniversity(true);
        request.setUserIds(List.of());
        request.setRegionIds(List.of(1L));
        request.setSingleTeamFriends(List.of(getSingleTeamFriendDto()));
        return request;
    }

    private static TeamRequest.SingleTeamFriendDto getSingleTeamFriendDto() {
        final TeamRequest.SingleTeamFriendDto singleTeamFriendDto = new TeamRequest.SingleTeamFriendDto();
        singleTeamFriendDto.setBirthYear(2005);
        singleTeamFriendDto.setNickname("친구닉네임");
        singleTeamFriendDto.setUniversityId(1L);
        singleTeamFriendDto.setProfileImageUrl("DEFAULT");
        return singleTeamFriendDto;
    }
}
