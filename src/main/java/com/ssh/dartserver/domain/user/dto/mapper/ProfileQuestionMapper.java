package com.ssh.dartserver.domain.user.dto.mapper;

import com.ssh.dartserver.domain.question.dto.QuestionResponse;
import com.ssh.dartserver.domain.user.dto.ProfileQuestionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileQuestionMapper {
    @Mapping(target = "questionResponse", source = "questionResponse")
    @Mapping(target = "count", source = "count")
    ProfileQuestionResponse toProfileQuestionResponse(QuestionResponse questionResponse, Long count);
}
