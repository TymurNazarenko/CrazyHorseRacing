package de.thb.crazyhorseracing.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    @Getter
    private final long id;
    private List<Wall> walls;
    @Getter
    private Carrot carrot;
    @Getter
    private int maxPlayers;
    private List<Vec> horseSpawnpoints;
    @Getter
    private String imagePath;

    public GameMap(long id, List<Wall> walls, Carrot carrot, int maxPlayers, List<Vec> horseSpawnpoints, String imagePath) {
        this.id = id;
        this.walls = walls;
        this.carrot = carrot;
        this.maxPlayers = maxPlayers;
        this.horseSpawnpoints = horseSpawnpoints;
        this.imagePath = imagePath;
    }

    public List<Wall> getWalls() {
        return new ArrayList<>(walls);
    }

    public List<Vec> getSpawnpoints() {
        return new ArrayList<>(horseSpawnpoints);
    }
}
