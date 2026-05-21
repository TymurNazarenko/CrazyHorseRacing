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
import java.util.List;
import java.util.Optional;

@Component
public class GameMapListLoader {
    @Getter
    private List<GameMap> maps;
    private ObjectMapper jsonMapper;

    public GameMap parseFile(String content) {
        // TODO throw exceptions here (when format is wrong) and handle them in the loop
        GameMapDTO levelRaw = jsonMapper.readValue(content, GameMapDTO.class);
        GameMap level = GameMapMapper.toDomain(levelRaw);
        return level;
    }

    @PostConstruct
    public void init() {
        maps = new ArrayList<>();
        jsonMapper = new ObjectMapper();

        File dir = new File("./src/main/resources/levels");
        File[] directoryListing = dir.listFiles();
        if (directoryListing == null) throw new IllegalStateException("Levels directory not found!");

        for (File f : directoryListing) {
            try {
                String content = Files.readString(Path.of(f.getPath()));
                GameMap gameMap = parseFile(content);
                if (gameMap == null) { throw new NullPointerException(); }
                maps.add(gameMap);
            } catch (IOException e) {
                System.err.println("Couldn't read level file: " + f.getName());
                System.err.println(e.getMessage());
            } catch (NullPointerException e) {
                System.err.println("Level turned null after being parsed: " + f.getName());
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.err.println("Something went wrong when parsing level: " + f.getName());
                System.err.println(e.getMessage());
            }
        }

        System.out.println("Loaded " + maps.size() + " levels");
    }

    public Optional<GameMap> getMapById(long id) {
        return maps.stream().filter(m -> m.id() == id).findFirst();
    }
}
