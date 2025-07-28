package de.superdupermarkt.shelflife.helper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSV {
    public static <T> Map<String, T> fetchMapFromCSV(Stream<String> lines, Function<String[], T> mapper, Function<T, String> keymapper) {
        return streamFromCSV(lines, mapper).collect(Collectors.toMap(keymapper, Function.identity()));
    }

    public static <T> List<T> fetchListFromCSV(Stream<String> lines, Function<String[], T> mapper) {
        return streamFromCSV(lines, mapper).toList();
    }

    private static <T> Stream<T> streamFromCSV(Stream<String> lines, Function<String[], T> mapper) {
        return lines
                .skip(1) //skip header
                .map(line -> line.split(";"))
                .map(mapper)
                .filter(Objects::nonNull);
    }
}
