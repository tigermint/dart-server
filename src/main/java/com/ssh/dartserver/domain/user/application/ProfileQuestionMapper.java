package com.ssh.dartserver.domain.user.application;

import com.ssh.dartserver.domain.question.presentation.response.QuestionResponse;
import com.ssh.dartserver.domain.user.presentation.v1.response.ProfileQuestionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProfileQuestionMapper {
    ProfileQuestionMapper INSTANCE = Mappers.getMapper(ProfileQuestionMapper.class);

    @Mapping(target = "questionResponse", source = "questionResponse")
    @Mapping(target = "count", source = "count")
    ProfileQuestionResponse toProfileQuestionResponse(QuestionResponse questionResponse, Long count);
}
