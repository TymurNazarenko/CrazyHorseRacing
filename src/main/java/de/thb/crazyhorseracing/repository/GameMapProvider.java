package de.thb.crazyhorseracing.repository;

import de.thb.crazyhorseracing.entity.*;
import de.thb.crazyhorseracing.service.RandomService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameMapProvider {
    @Getter
    private List<GameMap> maps;

    @PostConstruct
    public void load() {
        GenericJSONReader JSONReader = new GenericJSONReader();
        List<String> files = JSONReader.getJSONFilesInDirectoryDecorated("./src/main/resources/gamemaps", "GameMapProvider");
        maps = JSONReader.parseJSONFiles(files, GameMapDTO.class, new GameMapDTOMapper(), "GameMapProvider");
        System.out.println("Loaded " + maps.size() + " levels");
    }

    public GameMap getMapById(long id) {
        return maps.stream().filter(m -> m.id() == id).findFirst().orElseGet(() -> null);
    }

    public GameMap getRandomMap() {
        return maps.get(RandomService.nextInt(maps.size()));
    }
}
