package com.ssh.dartserver.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GuestInviteRequest {
    private String name;
    @NotNull
    private String phoneNumber;
    @NotNull
    private String questionContent;
}
