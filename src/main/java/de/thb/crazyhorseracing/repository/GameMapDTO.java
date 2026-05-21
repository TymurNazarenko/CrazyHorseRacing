package de.thb.crazyhorseracing.repository;

import java.util.List;

public record GameMapDTO(long id, List<List<Double>> carrot, List<List<Double>> spawnpoints, String imagePath, List<List<List<Double>>> walls, int maxPlayers) {}
