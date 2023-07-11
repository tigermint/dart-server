package com.ssh.dartserver.question.infra.mapper;

import com.ssh.dartserver.question.domain.Question;
import com.ssh.dartserver.question.dto.QuestionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(target = "questionId", source = "id")
    QuestionResponseDto toDto(Question  question);
}