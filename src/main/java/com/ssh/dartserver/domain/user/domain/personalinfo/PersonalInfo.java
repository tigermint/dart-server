package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@NoArgsConstructor
@Embeddable
public class PersonalInfo {
    @Embedded
    private Name name;

    @Embedded
    private Phone phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private AdmissionYear admissionYear;

    @Embedded
    private BirthYear birthYear;


    @Builder
    public PersonalInfo(Name name, Phone phone, Gender gender, AdmissionYear admissionYear, BirthYear birthYear) {
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.admissionYear = admissionYear;
        this.birthYear = birthYear;
    }
}
