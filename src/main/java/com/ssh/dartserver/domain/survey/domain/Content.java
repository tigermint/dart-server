package com.ssh.dartserver.domain.survey.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content {
    @Column(name = "content")
    private String value;

    private Content(String value) {
        validateLength(value);
        this.value = value;
    }

    public static Content from(String value) {
        return new Content(value);
    }
    private void validateLength(String value) {
        if (value.length() > 2000) {
            throw new IllegalArgumentException("2000자 이하로 입력해주세요.");
        }
    }


}
