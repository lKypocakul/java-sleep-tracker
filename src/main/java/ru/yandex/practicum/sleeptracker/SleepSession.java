package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public record SleepSession(LocalDateTime start, LocalDateTime end, SleepQuality quality) {

    public SleepSession {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        Objects.requireNonNull(quality, "quality must not be null");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(
                    "Момент пробуждения должен быть позже момента засыпания: " + start + " -> " + end);
        }
    }

    public long durationMinutes() {
        return Duration.between(start, end).toMinutes();
    }
}
