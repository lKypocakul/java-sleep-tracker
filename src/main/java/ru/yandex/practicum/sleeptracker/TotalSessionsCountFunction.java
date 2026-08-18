package ru.yandex.practicum.sleeptracker;

import java.util.List;

/**
 * Считает общее количество сессий сна за представленный период.
 */
public class TotalSessionsCountFunction implements SleepAnalyticsFunction<Integer> {

    private static final String DESCRIPTION = "Общее количество сессий сна";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public Integer analyze(List<SleepSession> sessions) {
        return sessions.size();
    }
}
