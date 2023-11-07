package com.ssh.dartserver.domain.survey.service;

import com.ssh.dartserver.domain.survey.domain.Comment;
import com.ssh.dartserver.domain.survey.domain.Survey;
import com.ssh.dartserver.domain.survey.dto.SurveyResponse;
import com.ssh.dartserver.domain.survey.dto.mapper.SurveyMapper;
import com.ssh.dartserver.domain.survey.infra.SurveyRepository;
import com.ssh.dartserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;

    public SurveyResponse.ReadDto readSurvey(User user, Long surveyId) {
        return surveyRepository.findSurveyById(surveyId)
                .map(survey -> surveyMapper.toReadDto(
                        survey,
                        getTotalHeadCount(survey),
                        getAnswerDtos(survey),
                        getUserAnswerId(user, survey),
                        getCommentDtos(survey))
                )
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설문입니다."));
    }


    public Page<SurveyResponse.ListDto> listSurvey(User user, Pageable pageable) {
        Page<Survey> allVisibleSurveys = surveyRepository.findAllVisibleSurvey(pageable);
        List<Survey> visibleSurveys = allVisibleSurveys.getContent();

        List<SurveyResponse.ListDto> surveyResponseListDtos = visibleSurveys.stream()
                .map(survey -> surveyMapper.toListDto(
                                survey,
                                getTotalHeadCount(survey),
                                getAnswerDtos(survey),
                                getUserAnswerId(user, survey),
                                Optional.ofNullable(survey.getComments())
                                        .flatMap(comments -> comments.stream().reduce((first, second) -> second))
                                        .map(comment -> comment.getContent().getValue())
                                        .orElse(null)
                        )
                )
                .collect(Collectors.toList());

        return new PageImpl<>(surveyResponseListDtos, pageable, allVisibleSurveys.getTotalElements());
    }

    private List<SurveyResponse.ReadDto.CommentDto> getCommentDtos(Survey survey) {
        return survey.getComments().stream()
                .map(comment -> surveyMapper.toReadCommentDto(comment, 1, getReadUserDto(comment)))
                .collect(Collectors.toList());
    }

    private SurveyResponse.ReadDto.UserDto getReadUserDto(Comment comment) {
        return surveyMapper.toReadUserDto(comment.getUser(), surveyMapper.toReadUniversityDto(comment.getUser().getUniversity()));
    }

    private static Long getUserAnswerId(User user, Survey survey) {
        return survey.getAnswers().stream()
                .flatMap(answer -> answer.getAnswerUsers().stream()
                        .filter(answerUser -> answerUser.getUser().getId().equals(user.getId())))
                .findAny()
                .map(answerUser -> answerUser.getAnswer().getId())
                .orElse(null);
    }

    private static int getTotalHeadCount(Survey survey) {
        return survey.getAnswers().stream()
                .mapToInt(answer -> answer.getAnswerUsers().size())
                .sum();
    }

    private List<SurveyResponse.AnswerDto> getAnswerDtos(Survey survey) {
        return survey.getAnswers().stream()
                .map(answer -> surveyMapper.toListAnswerDto(answer, answer.getAnswerUsers().size()))
                .collect(Collectors.toList());
    }
}
