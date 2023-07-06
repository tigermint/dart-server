package com.ssh.dartserver.vote.dto.response;

import com.ssh.dartserver.question.dto.QuestionResponseDto;
import com.ssh.dartserver.user.dto.UserResponseDto;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ReceivedVoteResponseDto {
    private Long voteId;
    private QuestionResponseDto question;
    private UserResponseDto pickedUser;
    private LocalDateTime pickedTime;
}
