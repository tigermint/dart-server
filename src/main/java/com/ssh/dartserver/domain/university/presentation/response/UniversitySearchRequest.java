package com.ssh.dartserver.domain.university.presentation.response;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
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
