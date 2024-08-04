package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Nickname {

    private static final int MAX_LENGTH = 10;

    @Column(name = "nickname")
    private String value;

    private Nickname(String value) {
        validateLength(value);
        this.value = value;
    }

    public static Nickname createRandomNickname() {
        return new Nickname(RandomNicknameGenerator.generate(MAX_LENGTH));
    }

    public static Nickname from(String value) {
        return new Nickname(value);
    }

    private void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("닉네임은 %d글자 이하만 가능합니다.", MAX_LENGTH));
        }
    }
}
