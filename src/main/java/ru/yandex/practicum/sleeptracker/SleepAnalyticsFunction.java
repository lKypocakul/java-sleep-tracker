package ru.yandex.practicum.sleeptracker;

import java.util.List;

public interface SleepAnalyticsFunction<R> {

    String getDescription();

    R analyze(List<SleepSession> sessions);
}
