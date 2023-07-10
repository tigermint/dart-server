package com.ssh.dartserver.user.domain.personalinfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Gender {

    MALE("MALE", "남자"),
    FEMALE("FEMALE", "여자");

    private final String key;
    private final String title;
}
