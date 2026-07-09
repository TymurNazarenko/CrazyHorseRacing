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
import java.util.*;

@Component
public class HorseTypeProvider {
    @Getter
    private List<HorseType> horseTypes;
    private ObjectMapper jsonMapper;

    public HorseType parseFile(String content) {
        HorseTypeDTO horseTypeRaw = jsonMapper.readValue(content, HorseTypeDTO.class);
        HorseType horseType = HorseTypeDTOMapper.toDomain(horseTypeRaw);
        return horseType;
    }

    @PostConstruct
    public void init() {
        jsonMapper = new ObjectMapper();
        horseTypes = new ArrayList<>();

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

        if (!horseTypes.isEmpty()) {
            Player.setDefaultHorseType(getHorseById(1));
        }
    }
    public HorseType getHorseById(long id) {
        return horseTypes.stream().filter(h -> h.id() == id).findFirst().orElseGet(() -> null);
    }
}
