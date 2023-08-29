package com.ssh.dartserver.domain.vote.dto.mapper;

import com.ssh.dartserver.domain.question.dto.QuestionResponse;
import com.ssh.dartserver.domain.user.dto.UserWithUniversityResponse;
import com.ssh.dartserver.domain.vote.domain.Vote;
import com.ssh.dartserver.domain.vote.dto.ReceivedVoteDetailResponse;
import com.ssh.dartserver.domain.vote.dto.ReceivedVoteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VoteMapper {
    @Mapping(target = "voteId", source = "vote.id")
    @Mapping(target = "pickedTime", source = "vote.pickedTime")
    @Mapping(target = "question", source = "question")
    @Mapping(target = "pickingUser", source = "pickingUser")
    @Mapping(target = "candidates", source = "candidates")
    ReceivedVoteDetailResponse toReceivedVoteDetailResponse(Vote vote, QuestionResponse question, UserWithUniversityResponse pickingUser, List<UserWithUniversityResponse> candidates);

    @Mapping(target = "voteId", source = "vote.id")
    @Mapping(target = "pickedTime", source = "vote.pickedTime")
    @Mapping(target = "question", source = "question")
    @Mapping(target = "pickingUser", source = "pickingUser")
    ReceivedVoteResponse toReceivedVoteResponse(Vote vote, QuestionResponse question, UserWithUniversityResponse pickingUser);
}
