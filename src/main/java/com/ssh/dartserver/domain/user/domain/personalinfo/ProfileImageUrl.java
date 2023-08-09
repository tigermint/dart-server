package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
public class ProfileImageUrl {

    @Column(name = "profile_image_url")
    private String value;

    private ProfileImageUrl(String value) {
        this.value = value;
    }

    public static ProfileImageUrl from(String value) {
        return new ProfileImageUrl(value);
    }

}
