package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.Vec;
import de.thb.crazyhorseracing.entity.Hitbox;
import de.thb.crazyhorseracing.entity.HorseType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class HorseListLoader {
    private List<HorseType> horseTypes;

    private Optional<HorseType> parseFile(String content) {
        // TODO
        return Optional.empty();
    }

    public Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    @PostConstruct
    public void init() {
        horseTypes = new ArrayList<>();

        for (String fileName : listFilesUsingJavaIO("./src/main/resources/horse_types")) {
            System.out.println(fileName);
        }

        // TODO load these from files instead
        Hitbox basichitbox = new Hitbox(List.of(new Vec(-1,-1), new Vec(-1,1), new Vec(1,1), new Vec(1,-1)));
        horseTypes.add(new HorseType(1, "/images/pink.jpg", basichitbox));
        horseTypes.add(new HorseType(2, "/images/cyan.jpg", basichitbox));
        horseTypes = Collections.unmodifiableList(horseTypes);
    }
    public Optional<HorseType> getHorseById(long id) {
        return horseTypes.stream().filter(h -> h.id() == id).findFirst();
    }

    public List<HorseType> copyHorses() { // returns a shallow copy of the horses array. We don't return the original array to prevent manipulation of the list
        return new ArrayList<>(horseTypes);
    }
}
