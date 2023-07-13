package com.ssh.dartserver.user.domain.personalinfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@RequiredArgsConstructor
@Embeddable
public class BirthYear {

    @Column(name = "birth_year")
    private final int value;

    public BirthYear() {
        this.value = 0;
    }

    public static BirthYear newInstance(int value) {
        return new BirthYear(value);
    }
}
