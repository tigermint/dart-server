package com.ssh.dartserver.domain.user.domain.recommendcode;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Embeddable
public class RecommendationCode {
    private static final int DEFAULT_CODE_LENGTH = 8;
    private static final int MINIMUM_CODE_LENGTH = 1;

    @Column(name = "recommendation_code")
    private String value;

    public RecommendationCode(String value) {
        validate(value);
        this.value = value;
    }

    public static RecommendationCode generate(RandomRecommendCodeGeneratable randomGenerator) {
        String generator = randomGenerator.generator(DEFAULT_CODE_LENGTH);
        return new RecommendationCode(generator);
    }

    private void validate(String value) {
        if(value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("추천 코드는 null이거나 비어있을 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecommendationCode that = (RecommendationCode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
