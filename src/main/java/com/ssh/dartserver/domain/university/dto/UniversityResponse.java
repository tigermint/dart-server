package com.ssh.dartserver.domain.university.dto;

import lombok.Getter;

@Getter
public class UniversityResponse {
    private Long id;
    private String name;
    private String department;

    public UniversityResponse() {
    }

    public UniversityResponse(final String name) {
        this.name = name;
    }

    public UniversityResponse(final Long id, final String name, final String department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }
}
