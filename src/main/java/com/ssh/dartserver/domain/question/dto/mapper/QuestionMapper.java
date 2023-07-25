package com.ssh.dartserver.domain.question.dto.mapper;

import com.ssh.dartserver.domain.question.domain.Question;
import com.ssh.dartserver.domain.question.dto.QuestionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(target = "questionId", source = "id")
    QuestionResponse toDto(Question question);
}
