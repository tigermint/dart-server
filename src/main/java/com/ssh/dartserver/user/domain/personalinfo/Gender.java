package com.ssh.dartserver.user.domain.personalinfo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE("MALE"), FEMALE("FEMALE");
    private final String value;

    Gender(String value) {
        this.value = value;
    }
    @JsonCreator
    public static Gender from(String value) {
        for (Gender gender : values()) {
            if (gender.getValue().equals(value)) {
                return gender;
            }
        }
        return null;
    }
    @JsonValue
    public String getValue() {
        return value;
    }
}
