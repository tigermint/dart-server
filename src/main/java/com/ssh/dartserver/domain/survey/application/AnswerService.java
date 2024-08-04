package com.ssh.dartserver.domain.survey.application;

import com.ssh.dartserver.domain.survey.domain.Answer;
import com.ssh.dartserver.domain.survey.domain.AnswerUser;
import com.ssh.dartserver.domain.survey.presentation.request.AnswerRequest;
import com.ssh.dartserver.domain.survey.presentation.response.SurveyResponse;
import com.ssh.dartserver.domain.survey.infra.AnswerRepository;
import com.ssh.dartserver.domain.survey.infra.AnswerUserRepository;
import com.ssh.dartserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final SurveyService surveyService;

    private final AnswerRepository answerRepository;
    private final AnswerUserRepository answerUserRepository;

    public SurveyResponse.ReadDto createAnswer(User user, Long surveyId, AnswerRequest request) {
        Answer answer = answerRepository.findById(request.getAnswerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 선택지입니다."));

        validationExistingAnswer(user, answer);

        AnswerUser answerUser = AnswerUser.builder()
                .user(user)
                .answer(answer)
                .build();
        answerUserRepository.save(answerUser);

        return surveyService.readSurvey(user, surveyId);
    }

    public SurveyResponse.ReadDto updateAnswer(User user, Long surveyId, Long answerId, AnswerRequest request) {
        AnswerUser answerUser = answerUserRepository.findByUserIdAndAnswerId(user.getId(), answerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 답변입니다."));

        answerUser.updateAnswer(answerRepository.findById(request.getAnswerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 선택지입니다.")));

        return surveyService.readSurvey(user, surveyId);
    }

    private void validationExistingAnswer(User user, Answer answer) {
        answerUserRepository.findByUserIdAndAnswerId(user.getId(), answer.getId())
                .ifPresent(answerUser -> {
                    throw new IllegalArgumentException("이미 답변하였습니다.");
                });
    }
}
