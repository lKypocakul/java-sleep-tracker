package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class BadQualitySessionsCountFunction implements SleepAnalyticsFunction<Long> {

    @Override
    public String getDescription() {
        return "Количество сессий с плохим качеством сна";
    }

    @Override
    public Long analyze(List<SleepSession> sessions) {
        return sessions.stream()
                .filter(session -> session.quality() == SleepQuality.BAD)
                .count();
    }
}
