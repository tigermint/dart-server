package com.ssh.dartserver.domain.user.domain.studentverificationinfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor
public class StudentIdCardImageUrl {
    private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("^(https://).*");

    @Column(name = "student_id_card_image_url")
    private String value;



    public StudentIdCardImageUrl(String value) {
        validateUrl(value);
        this.value = value;
    }

    public static StudentIdCardImageUrl from(String value) {
        return new StudentIdCardImageUrl(value);
    }
    private void validateUrl(String value) {
        if(!IMAGE_URL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("학생증 이미지 URL은 https://...로 시작합니다.");
        }
    }
}
