package com.ssh.dartserver.domain.user.domain.personalinfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImageUrl {

    private static final String DEFAULT_PROFILE_IMAGE_URL = "DEFAULT";
    private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("^(https://).*");

    @Column(name = "profile_image_url")
    private String value;

    private ProfileImageUrl(String value) {
        validateUrl(value);
        this.value = value;
    }

    public static ProfileImageUrl newInstance() {
        return new ProfileImageUrl(DEFAULT_PROFILE_IMAGE_URL);
    }

    public static ProfileImageUrl from(String value) {
        return new ProfileImageUrl(value);
    }

    private void validateUrl(String value) {
        if(!value.equals(DEFAULT_PROFILE_IMAGE_URL) && !IMAGE_URL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("프로필 이미지 URL은 https://...로 시작합니다.");
        }
    }

}
