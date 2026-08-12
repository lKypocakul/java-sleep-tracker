package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class AverageSessionDurationFunction implements SleepAnalyticsFunction<Double> {

    @Override
    public String getDescription() {
        return "Средняя продолжительность сессии сна (мин)";
    }

    @Override
    public Double analyze(List<SleepSession> sessions) {
        return sessions.stream()
                .mapToLong(SleepSession::durationMinutes)
                .average()
                .orElse(0.0);
    }
}
