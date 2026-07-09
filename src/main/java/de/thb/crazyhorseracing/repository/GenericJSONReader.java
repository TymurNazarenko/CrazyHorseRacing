package de.thb.crazyhorseracing.repository;

import com.fasterxml.jackson.core.JacksonException;
import de.thb.crazyhorseracing.entity.GameMap;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GenericJSONReader {
    private final ObjectMapper jsonMapper;

    public GenericJSONReader() {
        jsonMapper = new  ObjectMapper();
    }

    // No logging
    public <F, T> T parseJSONContent(String content, Class<F> dtoClass, DTOMapper<F, T> dtoMapper) {
        F dto = jsonMapper.readValue(content, dtoClass);
        T result = dtoMapper.toDomain(dto);
        return result;
    }

    public <F, T> T parseJSONFile(String filePath, Class<F> dtoClass, DTOMapper<F, T> dtoMapper, String logger) {
        try {
            String content = Files.readString(Path.of(filePath));
            return parseJSONContent(content, dtoClass, dtoMapper);
        } catch (JacksonException e) {
            System.err.printf("[%s] Something went wrong while parsing the JSON file: %s%n", logger, filePath);
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.printf("[%s] Something went wrong while reading JSON file: %s%n", logger, filePath);
            System.err.println(e.getMessage());
        }

        return null;
    }

    public <F, T> List<T> parseJSONFiles(List<String> filePaths, Class<F> dtoClass, DTOMapper<F, T> dtoMapper, String logger) {
        List<T> results = new ArrayList<>();
        for (String filePath : filePaths) {
            try {
                T result = parseJSONFile(filePath, dtoClass, dtoMapper, logger);
                if (result == null) { throw new NullPointerException(); }
                results.add(result);
            } catch (NullPointerException e) {
                System.err.printf("[%s] Object turned null after being parsed: %s%n", logger, filePath);
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.err.printf("[%s] Something unknown went wrong while reading/parsing JSON file: %s%n", logger, filePath);
                System.err.println(e.getMessage());
            }
        }
        return results;
    }

    private List<String> getJSONFilesInDirectory(String path) throws IllegalArgumentException, IOException {
        Path directory = Path.of(path);

        if (!Files.exists(directory)) {
            throw new IllegalArgumentException("Path does not exist: " + path);
        }

        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Path is not a directory: " + path);
        }

        try (Stream<Path> paths = Files.list(Path.of(path))) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .map(Path::toString)
                    .toList();
        }
    }

    public List<String> getJSONFilesInDirectoryDecorated(String path, String logger) {
        try {
            return getJSONFilesInDirectory(path);
        } catch (Exception e) {
            System.err.printf("[%s] Error while getting JSON files inside directory: %s%n", path, logger);
            System.err.println(e.getMessage());
            return new ArrayList<>();
        }
    }
}
