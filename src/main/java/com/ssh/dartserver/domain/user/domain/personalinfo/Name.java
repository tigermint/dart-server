package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@RequiredArgsConstructor
@Embeddable
public class Name {
    @Column(name = "name")
    private final String value;

    public Name() {
        this.value = "";
    }

    public static Name newInstance(String value) {
        return new Name(value);
    }

}
