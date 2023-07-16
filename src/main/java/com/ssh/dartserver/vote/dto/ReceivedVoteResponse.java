package com.ssh.dartserver.vote.dto;

import com.ssh.dartserver.question.dto.QuestionResponse;
import com.ssh.dartserver.user.dto.UserWithUniversityResponse;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ReceivedVoteResponse {
    private Long voteId;
    private QuestionResponse question;
    private UserWithUniversityResponse pickedUser;
    private LocalDateTime pickedTime;
}
