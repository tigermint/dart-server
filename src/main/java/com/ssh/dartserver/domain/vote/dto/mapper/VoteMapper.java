package com.ssh.dartserver.domain.vote.dto.mapper;

import com.ssh.dartserver.domain.question.dto.QuestionResponse;
import com.ssh.dartserver.domain.user.dto.UserWithUniversityResponse;
import com.ssh.dartserver.domain.vote.dto.ReceivedVoteResponse;
import com.ssh.dartserver.domain.vote.domain.Vote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VoteMapper {
    @Mapping(target = "voteId", source = "vote.id")
    @Mapping(target = "pickedTime", source = "vote.pickedTime")
    @Mapping(target = "question", source = "question")
    @Mapping(target = "pickedUser", source = "pickedUser")
    ReceivedVoteResponse toReceivedVoteResponseDto(QuestionResponse question, UserWithUniversityResponse pickedUser, Vote vote);

}
