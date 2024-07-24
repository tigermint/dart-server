package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Name {
    @Column(name = "name")
    private String value;

    private Name(String value) {
        validateLength(value);
        this.value = value;
    }

    public static Name from(String value) {
        return new Name(value);
    }

    private void validateLength(String value) {
        if (value.length() > 5) {
            throw new IllegalArgumentException("이름은 5글자 이하만 가능합니다");
        }
    }

}
