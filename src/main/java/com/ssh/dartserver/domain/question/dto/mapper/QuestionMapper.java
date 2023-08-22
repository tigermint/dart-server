package com.ssh.dartserver.domain.question.dto.mapper;

import com.ssh.dartserver.domain.question.domain.Question;
import com.ssh.dartserver.domain.question.dto.QuestionResponse;
import com.ssh.dartserver.domain.question.dto.ReceivedQuestionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(target = "questionId", source = "id")
    QuestionResponse toQuestionResponse(Question question);

    @Mapping(target = "questionResponse", source = "questionResponse")
    @Mapping(target = "votedCount", source = "votedCount")
    ReceivedQuestionResponse toReceivedQuestionResponse(QuestionResponse questionResponse, Long votedCount);
}
