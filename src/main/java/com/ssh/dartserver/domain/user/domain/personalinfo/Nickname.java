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
        this.value = value;
    }

    public static Nickname from(String value) {
        return new Nickname(value);
    }

}
