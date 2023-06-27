package com.ssh.dartserver.auth.dto.request;

import lombok.Data;

@Data
public class LoginRequestsDto {
    private String username;
    private String password;
}
