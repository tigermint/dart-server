package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@RequiredArgsConstructor
@Embeddable
public class Phone {
    @Column(name = "phone")
    private final String value;

    public Phone() {
        this.value = "";
    }

    public static Phone newInstance(String value) {
        return new Phone(value);
    }
}
