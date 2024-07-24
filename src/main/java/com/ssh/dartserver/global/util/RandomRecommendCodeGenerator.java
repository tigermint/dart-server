package com.ssh.dartserver.global.util;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomRecommendCodeGenerator{
    private static final int DEFAULT_RANDOM_CODE_LENGTH = 8;
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();

    private RandomRecommendCodeGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static String generate() {
        return IntStream.range(0, DEFAULT_RANDOM_CODE_LENGTH)
                .mapToObj(i -> CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())))
                .map(String::valueOf)
                .collect(Collectors.joining(""));
    }
}
