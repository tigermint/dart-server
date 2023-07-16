package com.ssh.dartserver.friend.dto;

import com.ssh.dartserver.university.dto.UniversityResponse;
import lombok.Data;

@Data
public class FriendResponse {
    private Long userId;
    private String name;
    private int admissionYear;
    private UniversityResponse university;
}
