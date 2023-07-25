package com.ssh.dartserver.domain.friend.dto;

import com.ssh.dartserver.domain.university.dto.UniversityResponse;
import lombok.Data;

@Data
public class FriendResponse {
    private Long userId;
    private String name;
    private String gender;
    private int admissionYear;
    private UniversityResponse university;
}
