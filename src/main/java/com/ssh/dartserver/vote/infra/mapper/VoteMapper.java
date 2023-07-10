package com.ssh.dartserver.vote.infra.mapper;

import com.ssh.dartserver.question.dto.QuestionResponseDto;
import com.ssh.dartserver.user.dto.UserWithUniversityResponseDto;
import com.ssh.dartserver.vote.domain.Vote;
import com.ssh.dartserver.vote.dto.response.ReceivedVoteResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VoteMapper {
    @Mapping(target = "voteId", source = "vote.id")
    @Mapping(target = "pickedTime", source = "vote.pickedTime")
    @Mapping(target = "question", source = "question")
    @Mapping(target = "pickedUser", source = "pickedUser")
    ReceivedVoteResponseDto toReceivedVoteResponseDto(QuestionResponseDto question, UserWithUniversityResponseDto pickedUser, Vote vote);

}
