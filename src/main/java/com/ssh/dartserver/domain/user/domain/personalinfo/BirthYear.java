package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BirthYear {
    private static final int BIRTH_YEAR_MIN = 1995;
    private static final int BIRTH_YEAR_MAX = 2005;

    @Column(name = "birth_year")
    private int value;

    private BirthYear(int value) {
        validateBirthYear(value);
        this.value = value;
    }

    public static BirthYear from(int value) {
        return new BirthYear(value);
    }

    private void validateBirthYear(int value) {
        if (BIRTH_YEAR_MIN <= value && value <= BIRTH_YEAR_MAX) {
            return;
        }
        throw new IllegalArgumentException(
            String.format("생년은 %d ~ %d 사이의 값이어야 합니다. 현재값: %d", BIRTH_YEAR_MIN, BIRTH_YEAR_MAX, value)
        );
    }


}
