package com.ssh.dartserver.domain.vote.dto;

import com.ssh.dartserver.domain.question.dto.QuestionResponse;
import com.ssh.dartserver.domain.user.dto.UserWithUniversityResponse;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ReceivedVoteResponse {
    private Long voteId;
    private QuestionResponse question;
    private UserWithUniversityResponse pickedUser;
    private LocalDateTime pickedTime;
}
