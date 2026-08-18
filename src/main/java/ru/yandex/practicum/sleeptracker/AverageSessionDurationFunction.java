package ru.yandex.practicum.sleeptracker;

import java.util.List;

/**
 * Вычисляет среднюю продолжительность сессии сна (в минутах).
 */
public class AverageSessionDurationFunction implements SleepAnalyticsFunction<Double> {

    private static final String DESCRIPTION = "Средняя продолжительность сессии сна (мин)";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public Double analyze(List<SleepSession> sessions) {
        return sessions.stream()
                .mapToLong(SleepSession::durationMinutes)
                .average()
                .orElse(0.0);
    }
}
