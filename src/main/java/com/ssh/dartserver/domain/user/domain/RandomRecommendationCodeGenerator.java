package com.ssh.dartserver.domain.user.domain;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomRecommendationCodeGenerator {
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();

    private RandomRecommendationCodeGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static String generate(final int length) {
        return IntStream.range(0, length)
                .mapToObj(i -> CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())))
                .map(String::valueOf)
                .collect(Collectors.joining(""));
    }
}
