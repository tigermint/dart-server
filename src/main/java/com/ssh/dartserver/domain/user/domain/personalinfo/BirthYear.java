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
        validateBirthYear(value);
        this.value = value;
    }

    private void validateBirthYear(int value) {
        if (value < 1995 || value > 2004) {
            throw new IllegalArgumentException("생년은 1995 ~ 2004 사이의 값이어야 합니다.");
        }
    }


    public static BirthYear from(int value) {
        return new BirthYear(value);
    }

}
