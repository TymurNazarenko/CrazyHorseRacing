package de.thb.crazyhorseracing.repository;

import de.thb.crazyhorseracing.entity.*;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class GameMapProvider {
    @Getter
    private List<GameMap> maps;

    @PostConstruct
    public void load() {
        GenericJSONReader JSONReader = new GenericJSONReader();
        List<String> files = JSONReader.getJSONFilesInDirectoryDecorated("./src/main/resources/levels", "GameMapProvider");
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
