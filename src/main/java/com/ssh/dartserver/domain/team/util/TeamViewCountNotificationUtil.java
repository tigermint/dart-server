package com.ssh.dartserver.domain.team.util;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.global.infra.notification.PlatformNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamViewCountNotificationUtil {
    private static final List<Integer> VIEW_COUNT_MILESTONE = List.of(10, 30, 50, 100, 300, 500, 700, 1000, 3000, 5000);
    private static final String HEADINGS = "벌써 %d명이 봤어요 🎉🎉";
    private static final String CONTENTS = "%s팀이 인기가 많네요! 엔대생에서 확인해봐요 🤗";

    private final PlatformNotification platformNotification;

    public void postNotificationOnViewCountMileStone(Team team) {
        postNotificationOnViewCountMileStone(List.of(team));
    }

    public void postNotificationOnViewCountMileStone(List<Team> teams) {
        if (Objects.isNull(teams) || teams.isEmpty()) {
            log.info("전달된 팀이 없습니다. (teams는 null이거나 빈 리스트일 수 없습니다.)");
            return;
        }

        List<Team> teamsReachedMilestone = teams.stream()
                .filter(team -> VIEW_COUNT_MILESTONE.contains(team.getViewCount().getValue()))
                .toList();

        if (teamsReachedMilestone.isEmpty()) {
            log.debug("Push 알림을 전송할 팀이 없습니다.");
            return;
        }

        log.info("다음 팀에 조회수 Push알림을 전송합니다. TeamIds: {}", teamsReachedMilestone.stream().map(Team::getId).toList());
        teamsReachedMilestone.forEach(this::sendViewCountNotification);
    }

    private void sendViewCountNotification(Team team) {
        List<Long> userIds = getMemberIds(team);
        log.info("다음 유저에게 Push알림을 전송합니다. UserIds: {}", userIds);

        platformNotification.postNotificationSpecificDevice(
                userIds,
                String.format(HEADINGS, team.getViewCount().getValue()),
                String.format(CONTENTS, team.getName().getValue())
        );
    }

    private List<Long> getMemberIds(Team team) {
        List<Long> userIds = new ArrayList<>();

        // v1
        if (Objects.nonNull(team.getTeamUsers())) {
            userIds.addAll(team.getTeamUsers().stream()
                    .map(TeamUser::getUser)
                    .map(User::getId)
                    .toList());
        }

        // v2
        if (Objects.nonNull(team.getLeader())) {
            userIds.add(team.getLeader().getId());
        }
        return userIds;
    }

}
