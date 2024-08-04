package com.ssh.dartserver.domain.team.application;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.global.infra.notification.PlatformNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeamViewCountNotificationUtil {
    private static final List<Integer> VIEW_COUNT_MILESTONE = List.of(10, 30, 50, 100, 300, 500, 700, 1000, 3000, 5000);
    private static final String HEADINGS = "벌써 %d명이 봤어요 🎉🎉";
    private static final String CONTENTS = "%s팀이 인기가 많네요! 엔대생에서 확인해봐요 🤗";

    private final PlatformNotification platformNotification;

    public void postNotificationOnViewCountMileStone(List<Team> teams) {
        List<Team> teamsReachedMilestone = teams.stream()
                .filter(team -> VIEW_COUNT_MILESTONE.contains(team.getViewCount().getValue()))
                .collect(Collectors.toList());

        teamsReachedMilestone.forEach(team -> {
            List<Long> userIds = team.getTeamUsers().stream()
                    .map(TeamUser::getUser)
                    .map(User::getId)
                    .collect(Collectors.toList());
            platformNotification.postNotificationSpecificDevice(
                    userIds,
                    String.format(HEADINGS, team.getViewCount().getValue()),
                    String.format(CONTENTS, team.getName().getValue())
            );
        });
    }

}
