package com.ssh.dartserver.question.dto;

import lombok.Data;

@Data
public class QuestionResponseDto {
    private Long questionId;
    private String content;
    private String icon;
}