package com.ssh.dartserver.domain.user.domain;

import com.ssh.dartserver.global.util.RandomRecommendCodeGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendationCode {

    @Column(name = "recommendation_code")
    private String value;

    private RecommendationCode(String value) {
        validate(value);
        this.value = value;
    }

    public static RecommendationCode newInstance() {
        return new RecommendationCode(RandomRecommendCodeGenerator.generate());
    }

    public static RecommendationCode from(String value) {
        return new RecommendationCode(value);
    }

    private void validate(String value) {
        if(value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("추천 코드는 null이거나 비어있을 수 없습니다.");
        }
    }
}
