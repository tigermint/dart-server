package com.ssh.dartserver.domain.survey.dto.mapper;

import com.ssh.dartserver.domain.survey.domain.Answer;
import com.ssh.dartserver.domain.survey.domain.Comment;
import com.ssh.dartserver.domain.survey.domain.Survey;
import com.ssh.dartserver.domain.survey.dto.SurveyResponse;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SurveyMapper {

    //Read
    @Mapping(target = "surveyId", source = "survey.id")
    @Mapping(target = "category", source = "survey.category.name")
    @Mapping(target = "answers", source = "answers")
    @Mapping(target = "content", source = "survey.content.value")
    @Mapping(target = "comments", source = "comments")
    SurveyResponse.ReadDto toReadDto(Survey survey, Integer totalHeadCount, List<SurveyResponse.AnswerDto> answers, Long userAnswerId, List<SurveyResponse.ReadDto.CommentDto> comments);

    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "content", source = "comment.content.value")
    @Mapping(target = "user", source = "user")
    SurveyResponse.ReadDto.CommentDto toReadCommentDto(Comment comment, Integer like, SurveyResponse.ReadDto.UserDto user);
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "name", source = "user.personalInfo.name.value")
    @Mapping(target = "nickname", source = "user.personalInfo.nickname.value")
    @Mapping(target = "gender", source = "user.personalInfo.gender")
    @Mapping(target = "admissionYear", source = "user.personalInfo.admissionYear.value")
    SurveyResponse.ReadDto.UserDto toReadUserDto(User user, SurveyResponse.ReadDto.UniversityDto university);
    @Mapping(target = "universityId", source = "university.id")
    SurveyResponse.ReadDto.UniversityDto toReadUniversityDto(University university);


    //List
    @Mapping(target = "surveyId", source = "survey.id")
    @Mapping(target = "category", source = "survey.category.name")
    @Mapping(target = "content", source = "survey.content.value")
    @Mapping(target = "answers", source = "answers")
    SurveyResponse.ListDto toListDto(Survey survey, Integer totalHeadCount, List<SurveyResponse.AnswerDto> answers, Long userAnswerId, String latestComment);

    //Common
    @Mapping(target = "content", source = "answer.content.value")
    @Mapping(target = "answerId", source = "answer.id")
    SurveyResponse.AnswerDto toListAnswerDto(Answer answer, Integer headCount);
}
