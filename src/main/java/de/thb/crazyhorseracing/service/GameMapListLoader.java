package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class GameMapListLoader {
    private List<GameMap> maps;

    @PostConstruct
    public void init() {
        maps = new ArrayList<>();

        // TODO LOAD FROM FILES

        ArrayList<Vec> firstMapSpawnpoints = new ArrayList<>();
        firstMapSpawnpoints.add(new Vec(0, 0));
        firstMapSpawnpoints.add(new Vec(1, 1));
        ArrayList<Wall> firstMapWalls = new ArrayList<>();
        firstMapWalls.add(new Wall(new Hitbox(List.of(new Vec(0,0), new Vec(0,1), new Vec(1,1), new Vec(1,0)))));

        maps.add(new GameMap(
            1,
                firstMapWalls,
            new Carrot(null),
            2,
            firstMapSpawnpoints,
            "/images/level1.jpg"
        ));
    }

    public Optional<GameMap> getMapById(long id) {
        return maps.stream().filter(m -> m.getId() == id).findFirst();
    }

    public List<GameMap> copyMaps() {
        return new ArrayList<>(maps);
    }
}
