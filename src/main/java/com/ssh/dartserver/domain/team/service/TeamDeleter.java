package com.ssh.dartserver.domain.team.service;

import com.ssh.dartserver.domain.chat.infra.ChatRoomUserRepository;
import com.ssh.dartserver.domain.proposal.infra.ProposalRepository;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.team.infra.SingleTeamFriendRepository;
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.infra.TeamUserRepository;
import com.ssh.dartserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeamDeleter {

    private final ProposalRepository proposalRepository;
    private final TeamRepository teamRepository;
    private final SingleTeamFriendRepository singleTeamFriendRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamRegionRepository teamRegionRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    @Transactional
    public void deleteAllTeamAndRelatedData(User user) {
        final List<Team> teams = getListTeamsForUser(user);

        proposalRepository.updateRequestingOrRequestedTeamsToNullIn(teams);

        chatRoomUserRepository.deleteAllByUsersInBatch(getListUsersForTeams(teams));

        singleTeamFriendRepository.deleteAllByTeamsInBatch(teams);
        teamRegionRepository.deleteAllByTeamsInBatch(teams);
        teamUserRepository.deleteAllByTeamsInBatch(teams);
        teamRepository.deleteAllInBatch(teams);
    }

    private static List<User> getListUsersForTeams(final List<Team> teams) {
        return teams.stream()
                .map(Team::getTeamUsers)
                .flatMap(List::stream)
                .map(TeamUser::getUser)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Team> getListTeamsForUser(final User user) {
        return teamUserRepository.findAllByUser(user).stream()
                .map(TeamUser::getTeam)
                .distinct()
                .collect(Collectors.toList());
    }
}
