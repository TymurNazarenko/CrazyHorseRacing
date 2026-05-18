package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.*;

import java.util.List;

public class GameMapMapper {
    public static GameMap toDomain(GameMapDTO dto) {
        return new GameMap(
                dto.id(),
                mapWalls(dto.walls()),
                new Carrot(new Hitbox(mapVecList(dto.carrot()))),
                dto.maxPlayers(),
                mapVecList(dto.spawnpoints()),
                dto.imagePath()
        );
    }

    private static List<Wall> mapWalls(List<List<List<Double>>> walls) {
        return walls.stream()
                .map(points -> new Wall(new Hitbox(mapVecList(points))))
                .toList();
    }

    private static List<Vec> mapVecList(List<List<Double>> points) {
        return points.stream()
                .map(p -> new Vec(p.get(0), p.get(1)))
                .toList();
    }
}
