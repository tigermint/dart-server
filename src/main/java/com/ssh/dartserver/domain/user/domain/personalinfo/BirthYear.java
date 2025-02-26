package com.ssh.dartserver.domain.user.domain.personalinfo;

import com.ssh.dartserver.global.util.DateTimeUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BirthYear {
    private static final int MIN_AGE = 20;
    private static final int MAX_AGE = 30;

    @Column(name = "birth_year")
    private int value;

    private BirthYear(int value) {
        validateBirthYear(value);
        this.value = value;
    }

    public static BirthYear from(int value) {
        return new BirthYear(value);
    }

    private void validateBirthYear(final int value) {
        int currentYear = DateTimeUtil.nowFromZone().getYear();
        int minBirthYear = currentYear - MAX_AGE + 1;
        int maxBirthYear = currentYear - MIN_AGE + 1;

        if (value < minBirthYear && value > maxBirthYear) {
            throw new IllegalArgumentException(String.format("생년은 %d ~ %d 사이의 값이어야 합니다. 현재값: %d", minBirthYear, maxBirthYear, value));
        }
    }

    public int getAge() {
        return DateTimeUtil.nowFromZone().getYear() - value + 1;
    }
}
