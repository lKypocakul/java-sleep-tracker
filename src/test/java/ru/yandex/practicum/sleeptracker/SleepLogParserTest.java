package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SleepLogParserTest {

    @TempDir
    Path tempDir;

    @Test
    void parsesValidLogFile_intoSleepSessions() throws IOException {
        Path file = tempDir.resolve("log.csv");
        Files.writeString(file, String.join("\n",
                "2024-01-10 23:15,2024-01-11 07:05,GOOD",
                "2024-01-11 23:40,2024-01-12 06:50,NORMAL",
                "2024-01-12 14:00,2024-01-12 15:30,BAD"
        ), StandardCharsets.UTF_8);

        List<SleepSession> sessions = SleepLogParser.parse(file);

        assertEquals(3, sessions.size());
        assertEquals(new SleepSession(
                LocalDateTime.of(2024, 1, 10, 23, 15),
                LocalDateTime.of(2024, 1, 11, 7, 5),
                SleepQuality.GOOD), sessions.get(0));
        assertEquals(SleepQuality.BAD, sessions.get(2).quality());
    }

    @Test
    void skipsEmptyLinesAndComments() throws IOException {
        Path file = tempDir.resolve("log_with_comments.csv");
        Files.writeString(file, String.join("\n",
                "# sleep log for January",
                "",
                "2024-01-10 23:15,2024-01-11 07:05,GOOD",
                "   ",
                "# another comment",
                "2024-01-11 23:40,2024-01-12 06:50,NORMAL"
        ), StandardCharsets.UTF_8);

        List<SleepSession> sessions = SleepLogParser.parse(file);

        assertEquals(2, sessions.size());
    }

    @Test
    void throwsException_onMalformedLine() throws IOException {
        Path file = tempDir.resolve("bad_log.csv");
        Files.writeString(file, "2024-01-10 23:15,2024-01-11 07:05", StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> SleepLogParser.parse(file));
    }

    @Test
    void throwsException_onUnknownQuality() throws IOException {
        Path file = tempDir.resolve("bad_quality.csv");
        Files.writeString(file, "2024-01-10 23:15,2024-01-11 07:05,EXCELLENT", StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> SleepLogParser.parse(file));
    }

    @Test
    void throwsException_whenFileDoesNotExist() {
        Path missing = tempDir.resolve("does_not_exist.csv");

        assertThrows(IOException.class, () -> SleepLogParser.parse(missing));
    }
}
