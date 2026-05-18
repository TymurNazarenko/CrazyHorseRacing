package de.thb.crazyhorseracing.entity;

import java.util.List;

public record HorseTypeDTO(long id, String imagePath, List<List<Double>> hitbox) {}
