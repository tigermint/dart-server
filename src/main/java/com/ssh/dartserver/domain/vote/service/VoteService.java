package com.ssh.dartserver.domain.vote.service;

import com.ssh.dartserver.domain.question.domain.Question;
import com.ssh.dartserver.domain.question.dto.mapper.QuestionMapper;
import com.ssh.dartserver.domain.question.infra.QuestionRepository;
import com.ssh.dartserver.domain.university.dto.mapper.UniversityMapper;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.dto.mapper.UserMapper;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.domain.vote.domain.Candidate;
import com.ssh.dartserver.domain.vote.domain.Vote;
import com.ssh.dartserver.domain.vote.dto.ReceivedVoteDetailResponse;
import com.ssh.dartserver.domain.vote.dto.ReceivedVoteResponse;
import com.ssh.dartserver.domain.vote.dto.VoteResultRequest;
import com.ssh.dartserver.domain.vote.dto.mapper.VoteMapper;
import com.ssh.dartserver.domain.vote.infra.CandidateRepository;
import com.ssh.dartserver.domain.vote.infra.VoteRepository;
import com.ssh.dartserver.global.infra.notification.PlatformNotification;
import com.ssh.dartserver.global.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

    private static final int VOTE_PICK_POINT = 2;
    private static final int VOTED_PICKED_POINT = 1;
    private static final int PAGE_SIZE = 10;

    private final VoteRepository voteRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;

    private final VoteMapper voteMapper;
    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;
    private final UniversityMapper universityMapper;

    private final PlatformNotification notification;

    @Transactional
    public synchronized Long create(User user, VoteResultRequest request) {

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다."));
        List<User> candidates = userRepository.findAllByIdIn(request.getCandidateIds());

        User pickingUser = userRepository.findByIdForUpdate(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        User pickedUser = candidates.stream()
                .filter(candidate -> candidate.getId().equals(request.getPickedUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("투표 받은 유저가 존재하지 않는 후보자입니다."));


        Vote vote = Vote.builder()
                .candidates(candidates)
                .pickedTime(DateTimeUtil.nowFromZone())
                .pickingUser(pickingUser)
                .pickedUser(pickedUser)
                .question(question)
                .build();

        Vote savedVote = voteRepository.save(vote);
        candidateRepository.saveAll(savedVote.getCandidates().getValues());

        pickingUser.addPoint(VOTE_PICK_POINT);
        pickedUser.addPoint(VOTED_PICKED_POINT);
        postNotification(pickingUser,pickedUser);
        return savedVote.getId();
    }


    public ReceivedVoteDetailResponse readReceivedVote(User user, Long voteId) {
        Vote vote = voteRepository.findByPickedUserAndId(user, voteId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표입니다."));
        return getReceivedVoteResponse(vote);
    }

    public Page<ReceivedVoteResponse> listReceivedVote(User user, int page, String criteria) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<Vote> votes = voteRepository.findAllByPickedUser(user, pageable);

        return votes.map(vote ->
                voteMapper.toReceivedVoteResponse(
                        vote,
                        questionMapper.toQuestionResponse(vote.getQuestion()),
                        userMapper.toUserWithUniversityResponse(
                                userMapper.toUserResponse(vote.getPickingUser()),
                                universityMapper.toUniversityResponse(vote.getPickingUser().getUniversity())
                        )

                ));
    }

    private void postNotification(User pickingUser, User pickedUser) {
        String contents = String.format("%d학번 %s학생이 당신을 투표했어요. +%d점!",
                pickingUser.getPersonalInfo().getAdmissionYear().getValue() - 2000,
                pickingUser.getPersonalInfo().getGender().getKorValue(),
                VOTED_PICKED_POINT);
        //TODO: 비동기 처리 필요
        CompletableFuture.runAsync(() ->
            notification.postNotificationSpecificDevice(pickedUser.getId(), contents)
        );
    }

    private ReceivedVoteDetailResponse getReceivedVoteResponse(Vote vote) {
        Optional<User> optionalPickingUser = Optional.ofNullable(vote.getPickingUser());
        List<Optional<User>> optionalCandidateUsers = vote.getCandidates().getValues().stream()
                .map(Candidate::getUser)
                .map(Optional::ofNullable)
                .collect(Collectors.toList());

        return voteMapper.toReceivedVoteDetailResponse(
                vote,
                questionMapper.toQuestionResponse(vote.getQuestion()),
                userMapper.toUserWithUniversityResponse(
                        userMapper.toUserResponse(optionalPickingUser.orElse(null)),
                        universityMapper.toUniversityResponse(optionalPickingUser.map(User::getUniversity).orElse(null))
                ),
                optionalCandidateUsers.stream()
                        .map(candidateUser ->
                                userMapper.toUserWithUniversityResponse(
                                        userMapper.toUserResponse(candidateUser.orElse(null)),
                                        universityMapper.toUniversityResponse(candidateUser.map(User::getUniversity).orElse(null))
                                )
                        )
                        .collect(Collectors.toList())
        );
    }
}
