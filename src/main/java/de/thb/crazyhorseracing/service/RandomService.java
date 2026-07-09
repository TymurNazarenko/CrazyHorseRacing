package de.thb.crazyhorseracing.service;

import lombok.Getter;

import java.util.Random;
public class RandomService {
    @Getter
    private static final Random src = new Random();

    public static double nextDouble() {
        return src.nextDouble();
    }

    public static int nextInt(int bound) {
        return src.nextInt(bound);
    }

    public static boolean nextBoolean() {
        return src.nextBoolean();
    }
}
