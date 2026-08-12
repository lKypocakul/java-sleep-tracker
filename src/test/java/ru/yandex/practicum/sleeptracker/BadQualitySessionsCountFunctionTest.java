package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BadQualitySessionsCountFunctionTest {

    private final BadQualitySessionsCountFunction function = new BadQualitySessionsCountFunction();

    @Test
    void returnsZero_whenNoSessions() {
        assertEquals(0L, function.analyze(List.of()));
    }

    @Test
    void returnsZero_whenNoBadSessions() {
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 1, 23, 0),
                        LocalDateTime.of(2024, 1, 2, 7, 0),
                        SleepQuality.GOOD),
                new SleepSession(
                        LocalDateTime.of(2024, 1, 2, 23, 0),
                        LocalDateTime.of(2024, 1, 3, 6, 0),
                        SleepQuality.NORMAL)
        );

        assertEquals(0L, function.analyze(sessions));
    }

    @Test
    void countsOnlyBadSessions() {
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 1, 23, 0),
                        LocalDateTime.of(2024, 1, 2, 7, 0),
                        SleepQuality.GOOD),
                new SleepSession(
                        LocalDateTime.of(2024, 1, 2, 23, 0),
                        LocalDateTime.of(2024, 1, 3, 4, 0),
                        SleepQuality.BAD),
                new SleepSession(
                        LocalDateTime.of(2024, 1, 3, 22, 0),
                        LocalDateTime.of(2024, 1, 4, 5, 0),
                        SleepQuality.BAD),
                new SleepSession(
                        LocalDateTime.of(2024, 1, 4, 23, 0),
                        LocalDateTime.of(2024, 1, 5, 6, 0),
                        SleepQuality.NORMAL)
        );

        assertEquals(2L, function.analyze(sessions));
    }
}
