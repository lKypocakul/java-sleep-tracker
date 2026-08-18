package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Простой сквозной (end-to-end) тест: запускает main() на небольшом
 * файле лога и проверяет, что в выводе присутствуют результаты
 * всех зарегистрированных аналитических функций.
 */
class SleepTrackerAppTest {

    @TempDir
    Path tempDir;

    @Test
    void mainPrintsResultsOfAllAnalyticsFunctions() throws IOException {
        Path logFile = tempDir.resolve("sleep_log.txt");
        Files.writeString(logFile, String.join("\n",
                "2024-01-01 23:10,2024-01-02 06:50,GOOD",
                "2024-01-02 23:45,2024-01-03 06:20,NORMAL",
                "2024-01-04 00:30,2024-01-04 05:40,BAD"
        ), StandardCharsets.UTF_8);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        try {
            SleepTrackerApp.main(new String[]{logFile.toString()});
        } finally {
            System.setOut(originalOut);
        }

        String result = output.toString(StandardCharsets.UTF_8);

        assertTrue(result.contains("Общее количество сессий сна"));
        assertTrue(result.contains("Минимальная продолжительность сессии сна"));
        assertTrue(result.contains("Максимальная продолжительность сессии сна"));
        assertTrue(result.contains("Средняя продолжительность сессии сна"));
        assertTrue(result.contains("Количество сессий с плохим качеством сна"));
        assertTrue(result.contains("Количество бессонных ночей"));
        assertTrue(result.contains("Хронотип пользователя"));
    }

    @Test
    void mainPrintsUsageMessage_whenNoArgumentsProvided() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        try {
            SleepTrackerApp.main(new String[]{});
        } finally {
            System.setOut(originalOut);
        }

        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Использование"));
    }
}
