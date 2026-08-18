package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChronotypeFunctionTest {

    private final ChronotypeFunction function = new ChronotypeFunction();

    private static SleepSession lark(LocalDateTime day) {
        // Falls asleep before 22:00, wakes up before 07:00.
        return new SleepSession(day.withHour(21).withMinute(30), day.plusDays(1).withHour(6).withMinute(0), SleepQuality.GOOD);
    }

    private static SleepSession owl(LocalDateTime day) {
        // Falls asleep after 23:00, wakes up after 09:00.
        return new SleepSession(day.withHour(23).withMinute(30), day.plusDays(1).withHour(9).withMinute(15), SleepQuality.GOOD);
    }

    private static SleepSession dove(LocalDateTime day) {
        // Neither the "owl" nor the "lark" conditions are satisfied.
        return new SleepSession(day.withHour(22).withMinute(30), day.plusDays(1).withHour(8).withMinute(0), SleepQuality.NORMAL);
    }

    private static SleepSession daytimeNap(LocalDateTime day) {
        // Does not overlap the 00:00-06:00 window at all - must be ignored.
        return new SleepSession(day.withHour(13).withMinute(0), day.withHour(14).withMinute(0), SleepQuality.NORMAL);
    }

    @Test
    void returnsLark_whenMajorityOfNightsAreLark() {
        List<SleepSession> sessions = List.of(
                lark(LocalDateTime.of(2024, 1, 1, 0, 0)),
                lark(LocalDateTime.of(2024, 1, 2, 0, 0)),
                lark(LocalDateTime.of(2024, 1, 3, 0, 0)),
                owl(LocalDateTime.of(2024, 1, 4, 0, 0))
        );

        assertEquals(Chronotype.LARK, function.analyze(sessions));
    }

    @Test
    void returnsOwl_whenMajorityOfNightsAreOwl() {
        List<SleepSession> sessions = List.of(
                owl(LocalDateTime.of(2024, 1, 1, 0, 0)),
                owl(LocalDateTime.of(2024, 1, 2, 0, 0)),
                owl(LocalDateTime.of(2024, 1, 3, 0, 0)),
                lark(LocalDateTime.of(2024, 1, 4, 0, 0))
        );

        assertEquals(Chronotype.OWL, function.analyze(sessions));
    }

    @Test
    void returnsDove_whenOwlAndLarkCountsAreTied() {
        List<SleepSession> sessions = List.of(
                owl(LocalDateTime.of(2024, 1, 1, 0, 0)),
                owl(LocalDateTime.of(2024, 1, 2, 0, 0)),
                lark(LocalDateTime.of(2024, 1, 3, 0, 0)),
                lark(LocalDateTime.of(2024, 1, 4, 0, 0))
        );

        assertEquals(Chronotype.DOVE, function.analyze(sessions));
    }

    @Test
    void returnsDove_whenMajorityOfNightsAreDove() {
        List<SleepSession> sessions = List.of(
                dove(LocalDateTime.of(2024, 1, 1, 0, 0)),
                dove(LocalDateTime.of(2024, 1, 2, 0, 0)),
                owl(LocalDateTime.of(2024, 1, 3, 0, 0)),
                lark(LocalDateTime.of(2024, 1, 4, 0, 0))
        );

        assertEquals(Chronotype.DOVE, function.analyze(sessions));
    }

    @Test
    void returnsDove_whenThereAreNoNightSessionsAtAll() {
        List<SleepSession> sessions = List.of(
                daytimeNap(LocalDateTime.of(2024, 1, 1, 0, 0)),
                daytimeNap(LocalDateTime.of(2024, 1, 2, 0, 0))
        );

        assertEquals(Chronotype.DOVE, function.analyze(sessions));
    }

    @Test
    void ignoresDaytimeNaps_whenDeterminingChronotype() {
        List<SleepSession> sessions = List.of(
                lark(LocalDateTime.of(2024, 1, 1, 0, 0)),
                lark(LocalDateTime.of(2024, 1, 2, 0, 0)),
                daytimeNap(LocalDateTime.of(2024, 1, 3, 0, 0)),
                daytimeNap(LocalDateTime.of(2024, 1, 4, 0, 0))
        );

        assertEquals(Chronotype.LARK, function.analyze(sessions));
    }

    @Test
    void classifiesAsOwl_whenFallingAsleepAfterMidnight() {
        // Falling asleep at 00:xx is later in the night than 23:00, even though as a raw
        // LocalTime-of-day comparison 00:30 is numerically "before" 23:00. This is exactly
        // the midnight-rollover case that a naive LocalTime#isAfter/#isBefore comparison
        // gets wrong.
        List<SleepSession> sessions = List.of(
                new SleepSession(
                        LocalDateTime.of(2024, 1, 2, 0, 30),
                        LocalDateTime.of(2024, 1, 2, 10, 0),
                        SleepQuality.NORMAL),
                new SleepSession(
                        LocalDateTime.of(2024, 1, 3, 0, 45),
                        LocalDateTime.of(2024, 1, 3, 10, 30),
                        SleepQuality.NORMAL),
                new SleepSession(
                        LocalDateTime.of(2024, 1, 4, 0, 15),
                        LocalDateTime.of(2024, 1, 4, 9, 45),
                        SleepQuality.NORMAL)
        );

        assertEquals(Chronotype.OWL, function.analyze(sessions));
    }
}
