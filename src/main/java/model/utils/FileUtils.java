package model.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

public class FileUtils {

    public static Stream<String> readCSVFileAsStream(String filePath, String header) throws IOException {
        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        return readCSVFileAsStream(path, header);
    }

    public static Stream<String> readCSVFileAsStream(Path path, String header) throws IOException {
        if (!Files.exists(path)) {
            createOrAppendCSVFile(path, header, null);
        }
        return Files.lines(path);
    }

    public static void createOrAppendCSVFile(Path path, String header, String content) throws IOException {
        header = header + System.lineSeparator();
        content = (content == null) ? null : content + System.lineSeparator();
        if (!Files.exists(path)) {
            createCSVFile(path, header);
        } else {
            if (isEmptyFile(path)) {
                appendLineToFile(path, header);
            }
        }
        appendLineToFile(path, content);
    }

    public static void createCSVFile(Path path, String header) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, header.getBytes(), StandardOpenOption.CREATE);
    }

    public static Boolean isEmptyFile(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return (reader.readLine() == null);
        }
    }

    public static void appendLineToFile(Path path, String content) throws IOException {
        if (content == null) return;
        Files.write(path, content.getBytes(), StandardOpenOption.APPEND);
    }


}
