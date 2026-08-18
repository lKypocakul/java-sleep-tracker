package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TotalSessionsCountFunctionTest {

    private final TotalSessionsCountFunction function = new TotalSessionsCountFunction();

    @Test
    void returnsZero_whenNoSessions() {
        assertEquals(0, function.analyze(List.of()));
    }

    @Test
    void returnsThree_whenThreeSessionsPresent() {
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 1, 23, 0),
                        LocalDateTime.of(2024, 1, 2, 7, 0),
                        SleepQuality.GOOD),
                new SleepSession(
                        LocalDateTime.of(2024, 1, 2, 23, 0),
                        LocalDateTime.of(2024, 1, 3, 6, 30),
                        SleepQuality.NORMAL),
                new SleepSession(
                        LocalDateTime.of(2024, 1, 3, 13, 0),
                        LocalDateTime.of(2024, 1, 3, 14, 0),
                        SleepQuality.BAD)
        );

        assertEquals(3, function.analyze(sessions));
    }
}
