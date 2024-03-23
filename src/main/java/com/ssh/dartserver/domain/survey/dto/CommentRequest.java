package com.ssh.dartserver.domain.survey.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class CommentRequest {
    @NotBlank
    private String content;
}
