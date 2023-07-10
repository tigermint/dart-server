package com.ssh.dartserver.user.domain.personalinfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@RequiredArgsConstructor
@Embeddable
public class Phone {
    @Column(name = "phone")
    private String value;

    public Phone(String value) {
        this.value = value;
    }
}
