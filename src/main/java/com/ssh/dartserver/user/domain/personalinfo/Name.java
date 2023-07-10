package com.ssh.dartserver.user.domain.personalinfo;

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
        this.value = value;
    }
}
