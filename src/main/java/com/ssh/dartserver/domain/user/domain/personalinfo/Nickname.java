package com.ssh.dartserver.domain.user.domain.personalinfo;

import com.ssh.dartserver.global.util.RandomNicknameGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Nickname {
    @Column(name = "nickname")
    private String value;

    private Nickname(String value) {
        validateLength(value);
        this.value = value;
    }

    public static Nickname newInstance() {
        return new Nickname(RandomNicknameGenerator.generate());
    }

    public static Nickname from(String value) {
        return new Nickname(value);
    }

    private void validateLength(String value) {
        if (value.length() > 10) {
            throw new IllegalArgumentException("닉네임은 10글자 이하만 가능합니다.");
        }
    }

}
