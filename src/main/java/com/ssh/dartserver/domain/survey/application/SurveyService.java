package com.ssh.dartserver.domain.survey.application;

import com.ssh.dartserver.domain.survey.domain.Comment;
import com.ssh.dartserver.domain.survey.domain.CommentLike;
import com.ssh.dartserver.domain.survey.domain.CommentReport;
import com.ssh.dartserver.domain.survey.domain.Survey;
import com.ssh.dartserver.domain.survey.presentation.response.SurveyResponse;
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
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설문입니다."));
        return surveyMapper.toReadDto(
                survey,
                getTotalHeadCount(survey),
                getAnswerDtos(survey),
                getUserAnswerId(user, survey),
                getCommentDtos(user, survey));
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

    private List<SurveyResponse.ReadDto.CommentDto> getCommentDtos(User user, Survey survey) {
        return survey.getComments().stream()
                .map(comment -> {
                    List<CommentLike> commentLikes = comment.getCommentLikes();
                    List<CommentReport> commentReports = comment.getCommentReports();

                    return surveyMapper.toReadCommentDto(
                            comment,
                            commentLikes.size(),
                            getIsLiked(user, commentLikes),
                            getIsReported(user, commentReports),
                            getReadUserDto(comment)
                    );
                })
                .collect(Collectors.toList());
    }

    private static boolean getIsReported(User user, List<CommentReport> commentReports) {
        return commentReports.stream()
                .anyMatch(commentReport -> commentReport.getUser() != null && commentReport.getUser().getId().equals(user.getId()));
    }

    private static boolean getIsLiked(User user, List<CommentLike> commentLikes) {
        return commentLikes.stream()
                .anyMatch(commentLike -> commentLike.getUser() != null && commentLike.getUser().getId().equals(user.getId()));
    }

    private SurveyResponse.ReadDto.UserDto getReadUserDto(Comment comment) {
        return Optional.ofNullable(comment.getUser())
                .map(user -> surveyMapper.toReadUserDto(user, surveyMapper.toReadUniversityDto(user.getUniversity()))).orElse(null);
    }

    private static Long getUserAnswerId(User user, Survey survey) {
        return survey.getAnswers().stream()
                .flatMap(answer -> answer.getAnswerUsers().stream()
                        .filter(answerUser -> answerUser.getUser() != null && answerUser.getUser().getId().equals(user.getId())))
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
