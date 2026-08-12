package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class MaxSessionDurationFunction implements SleepAnalyticsFunction<Long> {

    @Override
    public String getDescription() {
        return "Максимальная продолжительность сессии сна (мин)";
    }

    @Override
    public Long analyze(List<SleepSession> sessions) {
        return sessions.stream()
                .mapToLong(SleepSession::durationMinutes)
                .max()
                .orElse(0L);
    }
}
