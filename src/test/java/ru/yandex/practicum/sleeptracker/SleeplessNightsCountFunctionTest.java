package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SleeplessNightsCountFunctionTest {

    private final SleeplessNightsCountFunction function = new SleeplessNightsCountFunction();

    @Test
    void returnsZero_whenNoSessions() {
        assertEquals(0, function.analyze(List.of()));
    }

    @Test
    void returnsZero_forSingleNormalNight() {
        // Sleep 23:00 -> 07:00, clearly covering the 00:00-06:00 window.
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 1, 23, 0),
                        LocalDateTime.of(2024, 1, 2, 7, 0),
                        SleepQuality.GOOD)
        );

        assertEquals(0, function.analyze(sessions));
    }

    @Test
    void returnsOne_whenOnlyDaytimeSessionPresent() {
        // Sleep only from 7:00 to 11:00 - does not cover 00:00-06:00 at all.
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 5, 7, 0),
                        LocalDateTime.of(2024, 1, 5, 11, 0),
                        SleepQuality.NORMAL)
        );

        assertEquals(1, function.analyze(sessions));
    }

    @Test
    void returnsOne_whenThereIsAGapBetweenTwoNights() {
        // Night of Jan 2 is covered, night of Jan 3 is missing, night of Jan 4 is covered.
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 1, 23, 0),
                        LocalDateTime.of(2024, 1, 2, 6, 0),
                        SleepQuality.GOOD),
                new SleepSession(
                        LocalDateTime.of(2024, 1, 3, 23, 0),
                        LocalDateTime.of(2024, 1, 4, 6, 0),
                        SleepQuality.GOOD)
        );

        assertEquals(1, function.analyze(sessions));
    }

    @Test
    void returnsZero_whenSessionExactlyMatchesWindowBoundaries() {
        // Edge case: session exactly [00:00, 06:00) should still count as overlapping.
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 1, 0, 0),
                        LocalDateTime.of(2024, 1, 1, 6, 0),
                        SleepQuality.GOOD)
        );

        assertEquals(0, function.analyze(sessions));
    }

    @Test
    void handlesNoonBoundary_forFirstSession() {
        // Edge case: first (and only) session starts exactly at noon (not "after" noon),
        // so it should be attributed to the *previous* night, and the following night
        // (derived from the session's end time, which is after noon) is also sleepless.
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 1, 12, 0),
                        LocalDateTime.of(2024, 1, 1, 13, 0),
                        SleepQuality.NORMAL)
        );

        assertEquals(2, function.analyze(sessions));
    }

    @Test
    void returnsZero_whenEveryNightInRangeHasSleep() {
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 1, 23, 0),
                        LocalDateTime.of(2024, 1, 2, 7, 0),
                        SleepQuality.GOOD),
                new SleepSession(
                        LocalDateTime.of(2024, 1, 2, 23, 30),
                        LocalDateTime.of(2024, 1, 3, 6, 45),
                        SleepQuality.NORMAL),
                new SleepSession(
                        LocalDateTime.of(2024, 1, 3, 22, 0),
                        LocalDateTime.of(2024, 1, 4, 5, 30),
                        SleepQuality.GOOD)
        );

        assertEquals(0, function.analyze(sessions));
    }

    @Test
    void computesCorrectly_whenSessionsAreNotSortedByStartTime() {
        // Same scenario as returnsOne_whenThereIsAGapBetweenTwoNights, but the sessions are
        // listed out of chronological order. The function must not assume sessions.get(0)
        // is the earliest one.
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 3, 23, 0),
                        LocalDateTime.of(2024, 1, 4, 6, 0),
                        SleepQuality.GOOD),
                new SleepSession(
                        LocalDateTime.of(2024, 1, 1, 23, 0),
                        LocalDateTime.of(2024, 1, 2, 6, 0),
                        SleepQuality.GOOD)
        );

        assertEquals(1, function.analyze(sessions));
    }

    @Test
    void handlesLoggingIntervalThatCrossesAMonthBoundary() {
        // Logging interval spans January into February; the night of Feb 1st has no sleep.
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 30, 23, 0),
                        LocalDateTime.of(2024, 1, 31, 6, 0),
                        SleepQuality.GOOD),
                new SleepSession(
                        LocalDateTime.of(2024, 2, 1, 23, 0),
                        LocalDateTime.of(2024, 2, 2, 6, 0),
                        SleepQuality.GOOD)
        );

        assertEquals(1, function.analyze(sessions));
    }
}
