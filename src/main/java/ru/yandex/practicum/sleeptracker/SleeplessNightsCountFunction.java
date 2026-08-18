package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

/**
 * Считает количество бессонных ночей за период логирования.
 * <p>
 * Периодом логирования считается интервал от начала первой сессии сна
 * в файле до окончания последней (часы носятся не снимая).
 * Ночь считается "бессонной", если ни одна сессия сна не пересекает
 * интервал [00:00, 06:00) этой ночи.
 * <p>
 * Если первая сессия началась после полудня, отсчёт ночей начинается со
 * следующей ночи, если до полудня (включительно) — с предыдущей.
 */
public class SleeplessNightsCountFunction implements SleepAnalyticsFunction<Integer> {

    private static final LocalTime NOON = LocalTime.NOON;
    private static final LocalTime NIGHT_WINDOW_START = LocalTime.of(0, 0);
    private static final LocalTime NIGHT_WINDOW_END = LocalTime.of(6, 0);
    private static final String DESCRIPTION = "Количество бессонных ночей";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public Integer analyze(List<SleepSession> sessions) {
        if (sessions.isEmpty()) {
            return 0;
        }

        LocalDateTime firstStart = sessions.stream()
                .map(SleepSession::start)
                .min(LocalDateTime::compareTo)
                .orElseThrow();
        LocalDateTime lastEnd = sessions.stream()
                .map(SleepSession::end)
                .max(LocalDateTime::compareTo)
                .orElse(firstStart);

        LocalDate firstNight = nightDateFor(firstStart);
        LocalDate lastNight = nightDateFor(lastEnd);
        if (lastNight.isBefore(firstNight)) {
            lastNight = firstNight;
        }

        long nightsCount = ChronoUnit.DAYS.between(firstNight, lastNight) + 1;

        return (int) Stream.iterate(firstNight, date -> date.plusDays(1))
                .limit(nightsCount)
                .filter(night -> isSleepless(night, sessions))
                .count();
    }

    /**
     * Определяет календарную дату ночи, к которой относится момент времени,
     * согласно правилу: после полудня — следующая ночь, до полудня — предыдущая.
     */
    private static LocalDate nightDateFor(LocalDateTime dateTime) {
        return dateTime.toLocalTime().isAfter(NOON)
                ? dateTime.toLocalDate().plusDays(1)
                : dateTime.toLocalDate();
    }

    private static boolean isSleepless(LocalDate night, List<SleepSession> sessions) {
        LocalDateTime windowStart = LocalDateTime.of(night, NIGHT_WINDOW_START);
        LocalDateTime windowEnd = LocalDateTime.of(night, NIGHT_WINDOW_END);
        return sessions.stream().noneMatch(session ->
                session.start().isBefore(windowEnd) && session.end().isAfter(windowStart));
    }
}
