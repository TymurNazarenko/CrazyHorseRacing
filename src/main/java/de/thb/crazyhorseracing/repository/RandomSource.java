package de.thb.crazyhorseracing.repository;

import lombok.Getter;

import java.util.Random;
public class RandomSource {
    @Getter
    private static final Random src = new Random();
}
