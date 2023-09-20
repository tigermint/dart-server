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
        if (value.length() < 2 || value.length() > 10) {
            throw new IllegalArgumentException("닉네임은 2자 이상 10자 이하로 입력해주세요.");
        }
    }

    public static Nickname from(String value) {
        return new Nickname(value);
    }

}
