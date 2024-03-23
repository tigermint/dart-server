package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor
public class ProfileImageUrl {

    private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("^(https://).*");

    @Column(name = "profile_image_url")
    private String value;

    private ProfileImageUrl(String value) {
        validateUrl(value);
        this.value = value;
    }

    public static ProfileImageUrl from(String value) {
        return new ProfileImageUrl(value);
    }
    private void validateUrl(String value) {
        if (value.equals("DEFAULT")) {
            return;
        }
        if(!IMAGE_URL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("프로필 이미지 URL은 https://...로 시작합니다.");
        }
    }

}
