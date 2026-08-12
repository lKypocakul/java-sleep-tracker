package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AverageSessionDurationFunctionTest {

    private final AverageSessionDurationFunction function = new AverageSessionDurationFunction();

    @Test
    void returnsZero_whenNoSessions() {
        assertEquals(0.0, function.analyze(List.of()));
    }

    @Test
    void returnsAverage_ofSeveralSessions() {
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 1, 23, 0),
                        LocalDateTime.of(2024, 1, 2, 7, 0),
                        SleepQuality.GOOD),          // 480 min
                new SleepSession(
                        LocalDateTime.of(2024, 1, 2, 23, 0),
                        LocalDateTime.of(2024, 1, 3, 4, 0),
                        SleepQuality.NORMAL),         // 300 min
                new SleepSession(
                        LocalDateTime.of(2024, 1, 3, 22, 0),
                        LocalDateTime.of(2024, 1, 4, 8, 0),
                        SleepQuality.GOOD)            // 600 min
        );

        // (480 + 300 + 600) / 3 = 460.0
        assertEquals(460.0, function.analyze(sessions), 0.001);
    }

    @Test
    void returnsSameDuration_whenSingleSession() {
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 1, 23, 0),
                        LocalDateTime.of(2024, 1, 2, 6, 30),
                        SleepQuality.NORMAL)
        );

        assertEquals(450.0, function.analyze(sessions), 0.001);
    }
}
