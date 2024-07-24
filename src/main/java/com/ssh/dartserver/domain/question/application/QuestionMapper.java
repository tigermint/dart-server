package com.ssh.dartserver.domain.question.application;

import com.ssh.dartserver.domain.question.domain.Question;
import com.ssh.dartserver.domain.question.presentation.response.QuestionResponse;
import com.ssh.dartserver.domain.question.presentation.response.ReceivedQuestionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    @Mapping(target = "questionId", source = "id")
    QuestionResponse toQuestionResponse(Question question);

    @Mapping(target = "questionResponse", source = "questionResponse")
    @Mapping(target = "votedCount", source = "votedCount")
    ReceivedQuestionResponse toReceivedQuestionResponse(QuestionResponse questionResponse, Long votedCount);
}
