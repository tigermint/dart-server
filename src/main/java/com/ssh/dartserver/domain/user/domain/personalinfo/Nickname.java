package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class Nickname {
    @Column(name = "nickname")
    private String value;

    public Nickname(String value) {
        validateLength(value);
        this.value = value;
    }

    private void validateLength(String value) {
7        if (value.length() > 7) {
            throw new IllegalArgumentException("닉네임은 7글자 이하만 가능합니다.");
        }
    }

    public static Nickname from(String value) {
        return new Nickname(value);
    }

}
