package ru.yandex.practicum.sleeptracker;

import java.util.List;

/**
 * Считает количество сессий сна с плохим ({@link SleepQuality#BAD}) качеством.
 */
public class BadQualitySessionsCountFunction implements SleepAnalyticsFunction<Long> {

    private static final String DESCRIPTION = "Количество сессий с плохим качеством сна";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public Long analyze(List<SleepSession> sessions) {
        return sessions.stream()
                .filter(session -> session.quality() == SleepQuality.BAD)
                .count();
    }
}
