package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class Phone {
    @Column(name = "phone")
    private String value;

    private Phone(String value) {
        this.value = value;
    }

    public static Phone from(String value) {
        return new Phone(value);
    }

}
