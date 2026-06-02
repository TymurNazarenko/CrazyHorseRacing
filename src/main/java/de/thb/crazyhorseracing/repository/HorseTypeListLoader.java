package de.thb.crazyhorseracing.repository;

import de.thb.crazyhorseracing.entity.*;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class HorseTypeListLoader {
    private static final ObjectMapper jsonMapper  = new ObjectMapper();
    private static List<HorseType> horseTypes = new ArrayList<>();
    private static boolean loaded = false;

    public static HorseType parseFile(String content) {
        // TODO throw exceptions here (when format is wrong), also handle them in the loop
        HorseTypeDTO horseTypeRaw = jsonMapper.readValue(content, HorseTypeDTO.class);
        HorseType horseType = HorseTypeMapper.toDomain(horseTypeRaw);
        return horseType;
    }

    private static void init() {
        if (loaded) return; loaded = true;

        File dir = new File("./src/main/resources/horse_types");
        File[] directoryListing = dir.listFiles();
        if (directoryListing == null) throw new IllegalStateException("Horse types directory not found!");

        for (File f : directoryListing) {
            try {
                String content = Files.readString(Path.of(f.getPath()));
                HorseType horseType = parseFile(content);
                if (horseType == null) { throw new NullPointerException(); }
                horseTypes.add(horseType);
            } catch (IOException e) {
                System.err.println("Couldn't read horse type: " + f.getName());
                System.err.println(e.getMessage());
            } catch (NullPointerException e) {
                System.err.println("Horse type turned null after being parsed: " + f.getName());
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.err.println("Something went wrong when parsing horse type: " + f.getName());
                System.err.println(e.getMessage());
            }
        }

        System.out.println("Loaded " + horseTypes.size() + " horse types");
    }
    public static HorseType getHorseById(long id) {
        if (!loaded) init();
        return horseTypes.stream().filter(h -> h.id() == id).findFirst().orElseGet(() -> null);
    }

    public static List<HorseType> getHorseTypes() { // returns a shallow copy of the horses array. We don't return the original array to prevent manipulation of the list
        if (!loaded) init();
        return new ArrayList<>(horseTypes);
    }
}
