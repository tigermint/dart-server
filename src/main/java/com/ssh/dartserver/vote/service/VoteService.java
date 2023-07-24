package com.ssh.dartserver.vote.service;

import com.ssh.dartserver.common.utils.DateTimeUtils;
import com.ssh.dartserver.question.domain.Question;
import com.ssh.dartserver.question.infra.mapper.QuestionMapper;
import com.ssh.dartserver.question.infra.persistence.QuestionRepository;
import com.ssh.dartserver.university.infra.mapper.UniversityMapper;
import com.ssh.dartserver.user.domain.User;
import com.ssh.dartserver.user.infra.mapper.UserMapper;
import com.ssh.dartserver.user.infra.persistence.UserRepository;
import com.ssh.dartserver.vote.domain.Vote;
import com.ssh.dartserver.vote.dto.ReceivedVoteResponse;
import com.ssh.dartserver.vote.dto.VoteResultRequest;
import com.ssh.dartserver.vote.infra.mapper.VoteMapper;
import com.ssh.dartserver.vote.infra.persistence.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    private final VoteMapper voteMapper;
    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;
    private final UniversityMapper universityMapper;


    @Transactional
    public void create(User user, VoteResultRequest request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다."));

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
    }

    public ReceivedVoteResponse read(Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표입니다."));
        User pickedUser = userRepository.findById(vote.getPickedUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        return getReceivedVoteResponseDto(vote, pickedUser);
    }

    public List<ReceivedVoteResponse> list(User user) {
        List<ReceivedVoteResponse> dtos = new ArrayList<>();
        List<Vote> votes = voteRepository.findAllByPickedUserId(user.getId());
        votes.forEach(vote -> {
            ReceivedVoteResponse dto = getReceivedVoteResponseDto(vote, vote.getPickedUser());
            dtos.add(dto);
        });
        return dtos;
    }
    private ReceivedVoteResponse getReceivedVoteResponseDto(Vote vote, User pickedUser) {
        return voteMapper.toReceivedVoteResponseDto(
                questionMapper.toDto(vote.getQuestion()),
                userMapper.toUserWithUniversityResponseDto(userMapper.toUserResponseDto(pickedUser), universityMapper.toUniversityResponseDto(pickedUser.getUniversity())),
                vote
        );
    }
}
