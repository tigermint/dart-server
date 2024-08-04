package com.ssh.dartserver.domain.university.presentation.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UniversityResponse {
    private Long id;
    private String name;
    private String department;

    public UniversityResponse() {
    }

    public UniversityResponse(final String name) {
        this.name = name;
    }
}
