package com.ssh.dartserver.domain.university.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UniversitySearchRequest {
    @NotBlank @Size(max = 20)
    private String name;
    @Size(max = 20)
    private String department;
    @Positive @Max(30)
    private int size = 10;
}
