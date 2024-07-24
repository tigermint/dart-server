package com.ssh.dartserver.domain.question.application;

import com.ssh.dartserver.domain.question.domain.Question;
import com.ssh.dartserver.domain.question.presentation.response.QuestionResponse;
import com.ssh.dartserver.domain.question.presentation.response.ReceivedQuestionResponse;
import com.ssh.dartserver.domain.question.infra.QuestionRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.vote.domain.Vote;
import com.ssh.dartserver.domain.vote.infra.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final VoteRepository voteRepository;
    private final QuestionMapper questionMapper;

    public List<QuestionResponse> list() {
        List<Question> questions = questionRepository.findRandomQuestions();
        return questions.stream()
                .map(questionMapper::toQuestionResponse)
                .collect(Collectors.toList());
    }

    public List<ReceivedQuestionResponse> listReceivedVoteQuestion(User user) {

        List<Question> receivedVoteQuestions = voteRepository.findAllByPickedUser(user).stream()
                .map(Vote::getQuestion)
                .collect(Collectors.toList());

        Map<Question, Long> receivedVoteQuestionCountMap = receivedVoteQuestions.stream()
                .collect(Collectors.groupingBy(question -> question, Collectors.counting()));

        return receivedVoteQuestionCountMap.entrySet().stream()
                .map(entry -> questionMapper.toReceivedQuestionResponse(
                        questionMapper.toQuestionResponse(entry.getKey()),
                        entry.getValue()))
                .collect(Collectors.toList());
    }
}
