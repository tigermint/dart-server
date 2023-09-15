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
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamUserRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.studentverificationinfo.StudentIdCardVerificationStatus;
import com.ssh.dartserver.domain.user.domain.studentverificationinfo.StudentVerificationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ProposalRepository proposalRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamRegionRepository teamRegionRepository;

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

        chatRoomRepository.save(chatRoom);
        chatRoomUserRepository.saveAll(chatRoomUsers);
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


    private static boolean isStudentIdCardVerified(List<TeamUser> requestedTeamUsers) {
        return requestedTeamUsers.stream()
                .map(TeamUser::getUser)
                .map(User::getStudentVerificationInfo)
                .map(StudentVerificationInfo::getStudentIdCardVerificationStatus)
                .anyMatch(predicate -> predicate == StudentIdCardVerificationStatus.VERIFICATION_SUCCESS);
    }

    private ChatRoomResponse.ReadDto.TeamDto getReadTeamDto(Team team, List<TeamUser> teamUsers, List<TeamRegion> teamRegions) {
        return chatRoomMapper.toReadTeamDto(
                team,
                isStudentIdCardVerified(teamUsers),
                teamUsers.stream()
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
                        .collect(Collectors.toList()),
                teamRegions.stream()
                        .map(TeamRegion::getRegion)
                        .map(chatRoomMapper::toReadRegionDto)
                        .collect(Collectors.toList())
        );
    }

    private ChatRoomResponse.ListDto.TeamDto getListTeamDto(Team team, List<TeamUser> teamUsers, List<TeamRegion> teamRegions) {
        return chatRoomMapper.toListTeamDto(
                team,
                isStudentIdCardVerified(teamUsers),
                chatRoomMapper.toListUniversityDto(team.getUniversity()),
                teamUsers.stream()
                        .map(TeamUser::getUser)
                        .map(chatRoomMapper::toListUserDto)
                        .collect(Collectors.toList()),
                teamRegions.stream()
                        .map(TeamRegion::getRegion)
                        .map(chatRoomMapper::toListRegionDto)
                        .collect(Collectors.toList())
        );
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

    private void validateMeetStatus(Proposal proposal) {
        if (proposal.getProposalStatus() == ProposalStatus.PROPOSAL_SUCCESS) {
            throw new IllegalArgumentException("완료되지 않은 매칭입니다.");
        }
        if (proposal.getProposalStatus() == ProposalStatus.PROPOSAL_FAILED) {
            throw new IllegalArgumentException("실패한 매칭입니다.");
        }
    }
}
