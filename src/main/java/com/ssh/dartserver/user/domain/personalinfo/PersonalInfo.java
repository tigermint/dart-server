package com.ssh.dartserver.user.domain.personalinfo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Builder
    public PersonalInfo(Name name, Phone phone, Gender gender, AdmissionYear admissionYear) {
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.admissionYear = admissionYear;
    }
}