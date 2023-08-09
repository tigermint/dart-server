package com.ssh.dartserver.domain.user.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String nickname;
    private String phone;
    private String gender;
    private int admissionYear;
    private int birthYear;
    private String recommendationCode;
    private String profileImageUrl;
    private int point;
}
