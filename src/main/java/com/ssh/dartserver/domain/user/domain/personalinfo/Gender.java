package com.ssh.dartserver.domain.user.domain.personalinfo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE("MALE","남"), FEMALE("FEMALE", "여"), UNKNOWN("UNKNOWN", "알수없음");
    private final String value;
    private final String korValue;

    Gender(String value, String korValue) {
        this.value = value;
        this.korValue = korValue;
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

    public String getKorValue() {
        return korValue;
    }
}
