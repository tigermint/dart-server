package com.ssh.dartserver.user.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@NoArgsConstructor
@Embeddable
public class RecommendationCode {
    private static final int DEFAULT_CODE_LENGTH = 8;
    private static final int MINIMUM_CODE_LENGTH = 1;
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final Random RANDOM = new Random();

    @Column(name = "recommendation_code")
    private String value;

    public RecommendationCode(String value) {
        validate(value);
        this.value = value;
    }

    public static RecommendationCode generate() {
        return generate(DEFAULT_CODE_LENGTH);
    }

    public static RecommendationCode generate(int length) {
        if(length < MINIMUM_CODE_LENGTH) {
            throw new IllegalArgumentException("최소 길이는 1입니다.");
        }

        String generatedCode = IntStream.range(0, length)
                .mapToObj(i -> CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())))
                .map(String::valueOf)
                .collect(Collectors.joining(""));
        return new RecommendationCode(generatedCode);
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
