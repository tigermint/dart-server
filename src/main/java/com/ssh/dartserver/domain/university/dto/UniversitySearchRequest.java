package com.ssh.dartserver.domain.university.dto;

import javax.validation.constraints.NotBlank;
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
}
