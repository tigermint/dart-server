package com.ssh.dartserver.vote.dto.mapper;

import com.ssh.dartserver.question.dto.QuestionResponse;
import com.ssh.dartserver.user.dto.UserWithUniversityResponse;
import com.ssh.dartserver.vote.domain.Vote;
import com.ssh.dartserver.vote.dto.ReceivedVoteResponse;
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
