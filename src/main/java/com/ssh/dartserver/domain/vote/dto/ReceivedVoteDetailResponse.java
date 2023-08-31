package com.ssh.dartserver.domain.vote.dto;

import com.ssh.dartserver.domain.question.dto.QuestionResponse;
import com.ssh.dartserver.domain.user.dto.UserWithUniversityResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReceivedVoteDetailResponse {
    private Long voteId;
    private LocalDateTime pickedTime;
    private QuestionResponse question;
    private UserWithUniversityResponse pickingUser;
    private List<UserWithUniversityResponse> candidates;
}
