package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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


    @Builder
    private PersonalInfo(
            final Name name,
            final Nickname nickname,
            final Phone phone,
            final Gender gender,
            final AdmissionYear admissionYear,
            final BirthYear birthYear,
            final ProfileImageUrl profileImageUrl
    ) {
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.gender = gender;
        this.admissionYear = admissionYear;
        this.birthYear = birthYear;
        this.profileImageUrl = profileImageUrl;
    }

    public static PersonalInfo of(
            final String name,
            final String phone,
            final Gender gender,
            final int admissionYear,
            final int birthYear
    ) {
        return PersonalInfo.builder()
                .name(Name.from(name))
                .nickname(Nickname.createRandomNickname())
                .phone(Phone.from(phone))
                .profileImageUrl(ProfileImageUrl.newInstance())
                .gender(gender)
                .admissionYear(AdmissionYear.from(admissionYear))
                .birthYear(BirthYear.from(birthYear))
                .build();
    }

    public void updateNickname(String value) {
        this.nickname = Nickname.from(value);
    }

    public void updateProfileImageUrl(String value) {
        this.profileImageUrl = ProfileImageUrl.from(value);
    }
}

