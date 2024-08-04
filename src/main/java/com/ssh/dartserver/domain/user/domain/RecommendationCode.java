package com.ssh.dartserver.domain.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendationCode {

    private static final int DEFAULT_RANDOM_CODE_LENGTH = 8;

    @Column(name = "recommendation_code")
    private String value;

    private RecommendationCode(String value) {
        validate(value);
        validateLength(value);
        this.value = value;
    }

    public static RecommendationCode createRandomRecommendationCode() {
        return new RecommendationCode(RandomRecommendationCodeGenerator.generate(DEFAULT_RANDOM_CODE_LENGTH));
    }

    public static RecommendationCode from(String value) {
        return new RecommendationCode(value);
    }

    private void validate(String value) {
        if(value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("추천 코드는 null이거나 비어있을 수 없습니다.");
        }
    }

    private void validateLength(String value) {
        if (value.length() != DEFAULT_RANDOM_CODE_LENGTH) {
            throw new IllegalArgumentException(String.format("추천 코드는 %d글자여야 합니다.", DEFAULT_RANDOM_CODE_LENGTH));
        }
    }
}
