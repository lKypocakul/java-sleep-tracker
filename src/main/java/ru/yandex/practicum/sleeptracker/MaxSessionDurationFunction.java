package ru.yandex.practicum.sleeptracker;

import java.util.List;

/**
 * Вычисляет максимальную продолжительность сессии сна (в минутах).
 */
public class MaxSessionDurationFunction implements SleepAnalyticsFunction<Long> {

    private static final String DESCRIPTION = "Максимальная продолжительность сессии сна (мин)";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public Long analyze(List<SleepSession> sessions) {
        return sessions.stream()
                .mapToLong(SleepSession::durationMinutes)
                .max()
                .orElse(0L);
    }
}
