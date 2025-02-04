package com.ssh.dartserver.domain.team.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
public class Name {
    @Column(name = "name")
    private String value;

    public Name(String value) {
        validateLength(value);
        this.value = value;
    }

    private void validateLength(String value) {
        if (value.length() > 10) {
            throw new IllegalArgumentException("팀 이름은 10글자를 넘을 수 없습니다.");
        }
    }

    public static Name from(String value) {
        return new Name(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
