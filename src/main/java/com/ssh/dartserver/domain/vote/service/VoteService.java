package com.ssh.dartserver.domain.vote.service;

import com.ssh.dartserver.domain.question.domain.Question;
import com.ssh.dartserver.domain.question.dto.mapper.QuestionMapper;
import com.ssh.dartserver.domain.question.infra.QuestionRepository;
import com.ssh.dartserver.domain.university.dto.mapper.UniversityMapper;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.dto.mapper.UserMapper;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.domain.vote.domain.Vote;
import com.ssh.dartserver.domain.vote.dto.ReceivedVoteResponse;
import com.ssh.dartserver.domain.vote.dto.VoteResultRequest;
import com.ssh.dartserver.domain.vote.dto.mapper.VoteMapper;
import com.ssh.dartserver.domain.vote.infra.VoteRepository;
import com.ssh.dartserver.global.infra.notification.PlatformNotification;
import com.ssh.dartserver.global.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

    private static final int VOTE_PICK_POINT = 20;
    private static final int VOTED_PICKED_POINT = 10;

    private final VoteRepository voteRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    private final VoteMapper voteMapper;
    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;
    private final UniversityMapper universityMapper;

    private final PlatformNotification notification;


    @Transactional
    public void create(Long userId, VoteResultRequest request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        User pickedUser = userRepository.findById(request.getPickedUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));


        Vote vote = Vote.builder()
                .firstUserId(request.getFirstUserId())
                .secondUserId(request.getSecondUserId())
                .thirdUserId(request.getThirdUserId())
                .fourthUserId(request.getFourthUserId())
                .pickedTime(DateTimeUtils.nowFromZone())
                .pickedUser(pickedUser)
                .user(user)
                .question(question)
                .build();

        voteRepository.save(vote);
        addPoint(user, pickedUser);
        postNotification(user,pickedUser);
    }

    private void addPoint(User user, User pickedUser) {
        user.addPoint(VOTE_PICK_POINT);
        pickedUser.addPoint(VOTED_PICKED_POINT);
    }

    private void postNotification(User user, User pickedUser) {
        String contents = String.format("%d학번 %s학생이 당신을 투표했어요. +%d점!",
                user.getPersonalInfo().getAdmissionYear().getValue() - 2000,
                user.getPersonalInfo().getGender().getKorValue(),
                VOTED_PICKED_POINT);

        CompletableFuture.runAsync(() -> notification.postNotificationSpecificDevice(pickedUser.getId(), contents));
        //예외 발생 시 logger 추가 필요
    }

    public ReceivedVoteResponse read(Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표입니다."));
        return getReceivedVoteResponseDto(vote);
    }

    public List<ReceivedVoteResponse> list(User user) {
        List<ReceivedVoteResponse> dtos = new ArrayList<>();
        List<Vote> votes = voteRepository.findAllByPickedUserId(user.getId());
        votes.forEach(vote -> {
            ReceivedVoteResponse dto = getReceivedVoteResponseDto(vote);
            dtos.add(dto);
        });
        return dtos;
    }

    private ReceivedVoteResponse getReceivedVoteResponseDto(Vote vote) {
        Optional<User> optionalUser = Optional.ofNullable(vote.getUser());
        return voteMapper.toReceivedVoteResponseDto(
                questionMapper.toDto(vote.getQuestion()),
                userMapper.toUserWithUniversityResponseDto(
                        userMapper.toUserResponseDto(optionalUser.orElse(null)),
                        universityMapper.toUniversityResponseDto(optionalUser.map(User::getUniversity).orElse(null))
                ),
                vote
        );
    }
}
