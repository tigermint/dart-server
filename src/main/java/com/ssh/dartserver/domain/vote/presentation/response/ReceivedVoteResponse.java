package com.ssh.dartserver.domain.vote.presentation.response;

import com.ssh.dartserver.domain.question.presentation.response.QuestionResponse;
import com.ssh.dartserver.domain.user.presentation.v1.response.UserWithUniversityResponse;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ReceivedVoteResponse {
    private Long voteId;
    private LocalDateTime pickedTime;
    private QuestionResponse question;
    private UserWithUniversityResponse pickingUser;
}
