package ru.yandex.practicum.sleeptracker;

import java.util.List;

/**
 * Вычисляет минимальную продолжительность сессии сна (в минутах).
 */
public class MinSessionDurationFunction implements SleepAnalyticsFunction<Long> {

    private static final String DESCRIPTION = "Минимальная продолжительность сессии сна (мин)";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public Long analyze(List<SleepSession> sessions) {
        return sessions.stream()
                .mapToLong(SleepSession::durationMinutes)
                .min()
                .orElse(0L);
    }
}
