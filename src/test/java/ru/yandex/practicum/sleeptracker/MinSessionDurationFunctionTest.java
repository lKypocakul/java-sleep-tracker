package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MinSessionDurationFunctionTest {

    private final MinSessionDurationFunction function = new MinSessionDurationFunction();

    @Test
    void returnsZero_whenNoSessions() {
        assertEquals(0L, function.analyze(List.of()));
    }

    @Test
    void returnsShortestDuration_amongSeveralSessions() {
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 1, 23, 0),
                        LocalDateTime.of(2024, 1, 2, 7, 0),
                        SleepQuality.GOOD),          // 480 min
                new SleepSession(
                        LocalDateTime.of(2024, 1, 2, 23, 0),
                        LocalDateTime.of(2024, 1, 3, 4, 0),
                        SleepQuality.NORMAL),         // 300 min (shortest)
                new SleepSession(
                        LocalDateTime.of(2024, 1, 3, 22, 0),
                        LocalDateTime.of(2024, 1, 4, 8, 0),
                        SleepQuality.GOOD)            // 600 min
        );

        assertEquals(300L, function.analyze(sessions));
    }

    @Test
    void returnsExactDuration_whenSingleSession() {
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 1, 23, 0),
                        LocalDateTime.of(2024, 1, 2, 6, 30),
                        SleepQuality.NORMAL)
        );

        assertEquals(450L, function.analyze(sessions));
    }
}
