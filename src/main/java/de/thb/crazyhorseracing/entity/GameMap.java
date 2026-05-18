package de.thb.crazyhorseracing.entity;

import java.util.Collections;
import java.util.List;

public record GameMap(long id, List<Wall> walls, Carrot carrot, int maxPlayers, List<Vec> horseSpawnpoints, String imagePath) {
    public GameMap(long id, List<Wall> walls, Carrot carrot, int maxPlayers, List<Vec> horseSpawnpoints, String imagePath) {
        this.id = id;
        this.walls = Collections.unmodifiableList(walls);
        this.carrot = carrot;
        this.maxPlayers = maxPlayers;
        this.horseSpawnpoints = Collections.unmodifiableList(horseSpawnpoints);
        this.imagePath = imagePath;
    }
}
