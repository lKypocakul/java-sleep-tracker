package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class TotalSessionsCountFunction implements SleepAnalyticsFunction<Integer> {

    @Override
    public String getDescription() {
        return "Общее количество сессий сна";
    }

    @Override
    public Integer analyze(List<SleepSession> sessions) {
        return sessions.size();
    }
}
