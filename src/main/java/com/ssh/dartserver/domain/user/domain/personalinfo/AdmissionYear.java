package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdmissionYear {
    @Column(name = "admission_year")
    private int value;

    private AdmissionYear(int value) {
        this.value = value;
    }

    public static AdmissionYear from(int value) {
        return new AdmissionYear(value);
    }

}
