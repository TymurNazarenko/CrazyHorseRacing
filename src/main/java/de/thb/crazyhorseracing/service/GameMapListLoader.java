package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;
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

        File dir = new File("./src/main/resources/levels");
        File[] directoryListing = dir.listFiles();
        if (directoryListing == null) throw new IllegalStateException("Levels directory not found!");

        for (File f : directoryListing) {
            // Do something with child
        }

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
        return maps.stream().filter(m -> m.id() == id).findFirst();
    }

    public List<GameMap> copyMaps() {
        return new ArrayList<>(maps);
    }
}
