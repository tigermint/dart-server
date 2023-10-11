package com.ssh.dartserver.global.util;

import com.ssh.dartserver.domain.team.domain.SingleTeamFriend;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.team.infra.SingleTeamFriendRepository;
import com.ssh.dartserver.domain.user.domain.personalinfo.BirthYear;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TeamAverageAgeCalculator {
    private final SingleTeamFriendRepository singleTeamFriendRepository;

    public Double getAverageAge(List<TeamUser> teamUsers) {
        if (teamUsers.size() == 1) {
            return Stream.concat(
                            singleTeamFriendRepository.findAllByTeam(teamUsers.get(0).getTeam()).stream()
                                    .map(SingleTeamFriend::getBirthYear)
                                    .map(BirthYear::getValue),
                            Stream.of(teamUsers.get(0).getUser().getPersonalInfo().getBirthYear().getValue())
                    )
                    .collect(Collectors.averagingDouble(this::getAge));
        }
        return teamUsers.stream()
                .map(TeamUser::getUser)
                .map(user -> user.getPersonalInfo().getBirthYear().getValue())
                .collect(Collectors.averagingDouble(this::getAge));
    }

    private int getAge(int value) {
        return DateTimeUtil.nowFromZone().getYear() - value + 1;
    }
}
