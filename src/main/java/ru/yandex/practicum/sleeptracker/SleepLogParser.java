package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SleepLogParser {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private SleepLogParser() {
        // utility class
    }

    public static List<SleepSession> parse(Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .map(String::strip)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .map(SleepLogParser::parseLine)
                    .collect(Collectors.toUnmodifiableList());
        }
    }

    private static SleepSession parseLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Некорректная строка лога сна: \"" + line + "\"");
        }

        LocalDateTime start = LocalDateTime.parse(parts[0].strip(), DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(parts[1].strip(), DATE_TIME_FORMATTER);
        SleepQuality quality = SleepQuality.valueOf(parts[2].strip().toUpperCase());

        return new SleepSession(start, end, quality);
    }
}
