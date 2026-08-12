package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class MinSessionDurationFunction implements SleepAnalyticsFunction<Long> {

    @Override
    public String getDescription() {
        return "Минимальная продолжительность сессии сна (мин)";
    }

    @Override
    public Long analyze(List<SleepSession> sessions) {
        return sessions.stream()
                .mapToLong(SleepSession::durationMinutes)
                .min()
                .orElse(0L);
    }
}
