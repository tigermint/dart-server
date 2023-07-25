package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor
@Embeddable
public class AdmissionYear {
    @Column(name = "admission_year")
    private int value;

    public AdmissionYear(int value) {
        this.value = value;
    }

    public static AdmissionYear newInstance(int value) {
        return new AdmissionYear(value);
    }
}
