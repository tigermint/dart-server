package com.ssh.dartserver.user.domain.recommendcode;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@Component
public class RandomRecommendCodeGenerator implements RandomRecommendCodeGeneratable {

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();

    @Override
    public String generator(int length) {
        return IntStream.range(0, length)
                .mapToObj(i -> CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())))
                .map(String::valueOf)
                .collect(Collectors.joining(""));
    }
}
