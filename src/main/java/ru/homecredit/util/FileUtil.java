package ru.homecredit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {
    public static final String SYSTEM_SEPARATOR = File.separator;
    public static final String PROJECT_DIR = Paths.get(System.getProperty("user.dir")).toString();
    public static final String PROJECT_PATH = PROJECT_DIR + SYSTEM_SEPARATOR;
    public static final String FOLDER_TARGET = "\\target\\";
    public static final String FORMAT_XSD = ".xsd";

    public static List<String> searchInDirectory(File directory, String searchString, List<String> result) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory() && !file.getAbsolutePath().contains(FOLDER_TARGET)) {
                searchInDirectory(file, searchString, result);
            } else if (file.isFile() && file.getName().endsWith(FORMAT_XSD)) {
                searchInFile(file, searchString).ifPresent(result::add);
            }
        }
        return result;
    }

    @SneakyThrows
    public static Optional<String> searchInFile(File file, String searchString) {
        try (var reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().filter(line -> line.contains(searchString)).findFirst()
                    .map(line -> file.getAbsolutePath());
        }
    }

    @SneakyThrows
    public static void writeData(String filePath, String data) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(data);
            writer.flush();
        }
    }

    @SneakyThrows
    public static void deletePath(String filePath) {
        Path path = Paths.get(filePath);
        Files.delete(path);
    }

    @SneakyThrows
    public static String createFolderAndGetPath(String path) {
        Path dir = Path.of(PROJECT_DIR + path);
        if (!Files.exists(dir)) {
            Files.createDirectory(dir);
        }
        return dir + SYSTEM_SEPARATOR;
    }
}
