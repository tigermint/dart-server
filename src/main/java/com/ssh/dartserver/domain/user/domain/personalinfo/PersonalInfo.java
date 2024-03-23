package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class PersonalInfo {
    @Embedded
    private Name name;

    @Embedded
    private Nickname nickname;

    @Embedded
    private Phone phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private AdmissionYear admissionYear;

    @Embedded
    private BirthYear birthYear;

    @Embedded
    private ProfileImageUrl profileImageUrl;


    public void updateNickname(String value) {
        this.nickname = Nickname.from(value);
    }

    public void updateProfileImageUrl(String value) {
        this.profileImageUrl = ProfileImageUrl.from(value);
    }
}
