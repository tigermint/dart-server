package com.ssh.dartserver.friend.dto;

import com.ssh.dartserver.university.dto.UniversityDto;
import lombok.Data;

@Data
public class FriendResponseDto {
    private Long userId;
    private UniversityDto university;
    private int admissionNum;
    private String name;
}
