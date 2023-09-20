package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class Name {
    @Column(name = "name")
    private String value;

    public Name(String value) {
        validateLength(value);
        this.value = value;
    }

    private void validateLength(String value) {
        if (value.length() > 4) {
            throw new IllegalArgumentException("이름은 4글자 이하만 가능합니다");
        }
    }

    public static Name from(String value) {
        return new Name(value);
    }

}
