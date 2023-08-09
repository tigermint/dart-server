package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class BirthYear {

    @Column(name = "birth_year")
    private int value;

    public BirthYear(int value) {
        this.value = value;
    }

    public static BirthYear from(int value) {
        return new BirthYear(value);
    }

}
