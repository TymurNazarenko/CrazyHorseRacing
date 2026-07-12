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

    @PostConstruct
    public void load() {
        GenericJSONReader JSONReader = new GenericJSONReader();
        List<String> files = JSONReader.getJSONFilesInDirectoryDecorated("./src/main/resources/horses", "HorseTypeProvider");
        horseTypes = JSONReader.parseJSONFiles(files, HorseTypeDTO.class, new HorseTypeDTOMapper(), "HorseTypeProvider");
        System.out.println("Loaded " + horseTypes.size() + " horse types");

        if (!horseTypes.isEmpty()) {
            Player.setDefaultHorseType(getHorseById(1));
        }
    }

    public HorseType getHorseById(long id) {
        return horseTypes.stream().filter(h -> h.id() == id).findFirst().orElseGet(() -> null);
    }
}
