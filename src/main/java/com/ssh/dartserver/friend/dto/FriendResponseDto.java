package com.ssh.dartserver.friend.dto;

import com.ssh.dartserver.university.dto.UniversityResponseDto;
import lombok.Data;

@Data
public class FriendResponseDto {
    private Long userId;
    private String name;
    private int admissionYear;
    private UniversityResponseDto university;
}
