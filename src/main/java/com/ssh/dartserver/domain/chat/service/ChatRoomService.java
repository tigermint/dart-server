package com.ssh.dartserver.domain.chat.service;

import com.ssh.dartserver.domain.chat.domain.ChatRoom;
import com.ssh.dartserver.domain.chat.domain.ChatRoomUser;
import com.ssh.dartserver.domain.chat.dto.ChatRoomRequest;
import com.ssh.dartserver.domain.chat.dto.ChatRoomResponse;
import com.ssh.dartserver.domain.chat.dto.mapper.ChatRoomMapper;
import com.ssh.dartserver.domain.chat.infra.ChatRoomRepository;
import com.ssh.dartserver.domain.chat.presentation.ChatRoomUserRepository;
import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.domain.ProposalStatus;
import com.ssh.dartserver.domain.proposal.infra.ProposalRepository;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.team.infra.SingleTeamFriendRepository;
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamUserRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.profilequestions.ProfileQuestions;
import com.ssh.dartserver.domain.user.domain.studentverificationinfo.StudentIdCardVerificationStatus;
import com.ssh.dartserver.domain.user.domain.studentverificationinfo.StudentVerificationInfo;
import com.ssh.dartserver.global.infra.notification.PlatformNotification;
import com.ssh.dartserver.global.util.TeamAverageAgeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {
    private static final String CHAT_ROOM_OPEN_CONTENTS = "채팅방이 열렸어요, 과팅 약속 잡아봐요 😀";

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ProposalRepository proposalRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamRegionRepository teamRegionRepository;
    private final SingleTeamFriendRepository singleTeamFriendRepository;
    private final TeamAverageAgeCalculator teamAverageAgeCalculator;

    private final PlatformNotification notification;

    private final ChatRoomMapper chatRoomMapper;

    @Transactional
    public Long createChatRoom(ChatRoomRequest.Create request) {
        Proposal proposal = proposalRepository.findById(request.getProposalId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미팅 제안입니다."));

        validateMeetStatus(proposal);

        Team requestingTeam = proposal.getRequestingTeam();
        Team requestedTeam = proposal.getRequestedTeam();

        List<User> users = teamUserRepository.findAllByTeamIn(List.of(requestingTeam, requestedTeam)).stream()
                .map(TeamUser::getUser)
                .collect(Collectors.toList());

        ChatRoom chatRoom = ChatRoom.builder()
                .latestChatMessageContent(null)
                .latestChatMessageTime(null)
                .proposal(proposal)
                .build();

        List<ChatRoomUser> chatRoomUsers = users.stream()
                .map(user -> ChatRoomUser.builder()
                        .chatRoom(chatRoom)
                        .user(user)
                        .build())
                .collect(Collectors.toList());

        proposal.updateProposalStatus(ProposalStatus.PROPOSAL_SUCCESS);
        chatRoomRepository.save(chatRoom);
        chatRoomUserRepository.saveAll(chatRoomUsers);

        List<Long> chatRoomUserIds = chatRoomUsers.stream()
                .map(ChatRoomUser::getUser)
                .map(User::getId)
                .collect(Collectors.toList());

        CompletableFuture.runAsync(() ->
                notification.postNotificationSpecificDevice(chatRoomUserIds, CHAT_ROOM_OPEN_CONTENTS)
        );
        return chatRoom.getId();
    }

    public ChatRoomResponse.ReadDto readChatRoom(Long chatRoomId, User user) {
        List<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findAllByChatRoomId(chatRoomId);
        ChatRoomUser chatRoomUser = chatRoomUsers.stream()
                .filter(u -> u.getUser().getId().equals(user.getId()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("채팅방에 속해있지 않은 유저입니다."));

        ChatRoom chatRoom = chatRoomUser.getChatRoom();

        Team requestingTeam = chatRoom.getProposal().getRequestingTeam();
        Team requestedTeam = chatRoom.getProposal().getRequestedTeam();

        List<TeamUser> requestingTeamUsers = teamUserRepository.findAllByTeam(requestingTeam);
        List<TeamUser> requestedTeamUsers = teamUserRepository.findAllByTeam(requestedTeam);

        List<TeamRegion> requestingTeamRegions = teamRegionRepository.findAllByTeam(requestingTeam);
        List<TeamRegion> requestedTeamRegions = teamRegionRepository.findAllByTeam(requestedTeam);

        return chatRoomMapper.toReadDto(
                chatRoom,
                getReadTeamDto(
                        requestingTeam,
                        getTeamUsersInChatRoom(chatRoomUsers, requestingTeamUsers),
                        requestingTeamRegions
                ),
                getReadTeamDto(
                        requestedTeam,
                        getTeamUsersInChatRoom(chatRoomUsers, requestedTeamUsers),
                        requestedTeamRegions
                )
        );
    }


    public List<ChatRoomResponse.ListDto> listChatRoom(User user) {
        List<ChatRoomUser> myChatRoomUsers = chatRoomUserRepository.findAllByUser(user);
        List<ChatRoom> myChatRooms = myChatRoomUsers.stream()
                .map(ChatRoomUser::getChatRoom)
                .distinct()
                .collect(Collectors.toList());

        return myChatRooms.stream()
                .map(chatRoom -> {
                    Team requestingTeam = chatRoom.getProposal().getRequestingTeam();
                    Team requestedTeam = chatRoom.getProposal().getRequestedTeam();

                    List<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findAllByChatRoomId(chatRoom.getId());

                    return chatRoomMapper.toListDto(
                            chatRoom,
                            getListTeamDto(
                                    requestingTeam,
                                    getTeamUsersInChatRoom(chatRoomUsers, teamUserRepository.findAllByTeam(requestingTeam)),
                                    teamRegionRepository.findAllByTeam(requestingTeam)),
                            getListTeamDto(
                                    requestedTeam,
                                    getTeamUsersInChatRoom(chatRoomUsers, teamUserRepository.findAllByTeam(requestedTeam)),
                                    teamRegionRepository.findAllByTeam(requestedTeam)
                            )
                    );
                })
                .collect(Collectors.toList());
    }


    private ChatRoomResponse.ReadDto.TeamDto getReadTeamDto(Team team, List<TeamUser> teamUsers, List<TeamRegion> teamRegions) {
        return Optional.ofNullable(team)
                .map(t -> chatRoomMapper.toReadTeamDto(
                        t,
                        isStudentIdCardVerified(teamUsers),
                        teamAverageAgeCalculator.getAverageAge(teamUsers),
                        Optional.of(teamUsers)
                                .filter(users -> users.size() == 1)
                                .map(users -> getReadSingleTeamUserDto(t, users))
                                .orElseGet(() -> getReadMultipleTeamUserDto(teamUsers)),
                        teamRegions.stream()
                                .map(TeamRegion::getRegion)
                                .map(chatRoomMapper::toReadRegionDto)
                                .collect(Collectors.toList())
                ))
                .orElse(null);
    }


    private ChatRoomResponse.ListDto.TeamDto getListTeamDto(Team team, List<TeamUser> teamUsers, List<TeamRegion> teamRegions) {
        return Optional.ofNullable(team)
                .map(t -> chatRoomMapper.toListTeamDto(
                        t,
                        isStudentIdCardVerified(teamUsers),
                        chatRoomMapper.toListUniversityDto(t.getUniversity()),
                        Optional.of(teamUsers)
                                .filter(users -> users.size() == 1)
                                .map(users -> getListSingleTeamUserDto(t, users))
                                .orElseGet(() -> getListMultipleTeamUserDto(teamUsers)),
                        teamRegions.stream()
                                .map(TeamRegion::getRegion)
                                .map(chatRoomMapper::toListRegionDto)
                                .collect(Collectors.toList())
                ))
                .orElse(null);
    }

    private List<ChatRoomResponse.ReadDto.UserDto> getReadSingleTeamUserDto(Team team, List<TeamUser> teamUsers) {
        return Stream.concat(
                        teamUsers.stream()
                                .map(TeamUser::getUser),
                        singleTeamFriendRepository.findAllByTeam(team).stream()
                                .map(singleTeamFriend ->
                                        User.createSingleTeamFriendUser(
                                                singleTeamFriend.getNickname().getValue(),
                                                singleTeamFriend.getBirthYear().getValue(),
                                                singleTeamFriend.getProfileImageUrl().getValue(),
                                                singleTeamFriend.getUniversity()
                                        )
                                )
                )
                .map(teamUser ->
                        chatRoomMapper.toReadUserDto(
                                teamUser,
                                chatRoomMapper.toReadUniversityDto(teamUser.getUniversity()),
                                Optional.ofNullable(teamUser.getProfileQuestions())
                                        .map(ProfileQuestions::getValues)
                                        .orElse(Collections.emptyList())
                                        .stream()
                                        .map(profileQuestion -> chatRoomMapper.toReadProfileQuestionDto(
                                                profileQuestion,
                                                chatRoomMapper.toReadQuestionDto(profileQuestion.getQuestion())
                                        ))
                                        .collect(Collectors.toList())
                        )
                )
                .collect(Collectors.toList());
    }

    private List<ChatRoomResponse.ReadDto.UserDto> getReadMultipleTeamUserDto(List<TeamUser> teamUsers) {
        return teamUsers.stream()
                .map(TeamUser::getUser)
                .map(user -> chatRoomMapper.toReadUserDto(
                        user,
                        chatRoomMapper.toReadUniversityDto(user.getUniversity()),
                        user.getProfileQuestions().getValues().stream()
                                .map(profileQuestion -> chatRoomMapper.toReadProfileQuestionDto(
                                                profileQuestion,
                                                chatRoomMapper.toReadQuestionDto(profileQuestion.getQuestion())
                                        )
                                ).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    private List<ChatRoomResponse.ListDto.UserDto> getListSingleTeamUserDto(Team team, List<TeamUser> users) {
        return Stream.concat(
                        users.stream()
                                .map(TeamUser::getUser),
                        singleTeamFriendRepository.findAllByTeam(team).stream()
                                .map(singleTeamFriend ->
                                        User.createSingleTeamFriendUser(
                                                singleTeamFriend.getNickname().getValue(),
                                                singleTeamFriend.getBirthYear().getValue(),
                                                singleTeamFriend.getProfileImageUrl().getValue(),
                                                singleTeamFriend.getUniversity()
                                        )
                                )
                )
                .map(chatRoomMapper::toListUserDto)
                .collect(Collectors.toList());

    }

    private List<ChatRoomResponse.ListDto.UserDto> getListMultipleTeamUserDto(List<TeamUser> teamUsers) {
        return teamUsers.stream()
                .map(TeamUser::getUser)
                .map(chatRoomMapper::toListUserDto)
                .collect(Collectors.toList());
    }

    private static List<TeamUser> getTeamUsersInChatRoom(List<ChatRoomUser> chatRoomUsers, List<TeamUser> requestingTeamUsers) {
        List<Long> chatRoomUserIds = chatRoomUsers.stream()
                .map(ChatRoomUser::getUser)
                .map(User::getId)
                .collect(Collectors.toList());
        return requestingTeamUsers.stream()
                .filter(teamUser -> chatRoomUserIds.contains(teamUser.getUser().getId()))
                .collect(Collectors.toList());
    }

    private static boolean isStudentIdCardVerified(List<TeamUser> requestedTeamUsers) {
        return requestedTeamUsers.stream()
                .map(TeamUser::getUser)
                .map(User::getStudentVerificationInfo)
                .map(StudentVerificationInfo::getStudentIdCardVerificationStatus)
                .anyMatch(predicate -> predicate == StudentIdCardVerificationStatus.VERIFICATION_SUCCESS);
    }


    private void validateMeetStatus(Proposal proposal) {
        if (proposal.getProposalStatus().isSuccess()) {
            throw new IllegalArgumentException("이미 성공한 매칭입니다.");
        }
        if (proposal.getProposalStatus().isFailed()) {
            throw new IllegalArgumentException("실패한 매칭입니다.");
        }
    }


}
