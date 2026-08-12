package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChronotypeFunction implements SleepAnalyticsFunction<ChronoType> {

    private static final LocalTime OWL_SLEEP_AFTER = LocalTime.of(23, 0);
    private static final LocalTime OWL_WAKE_AFTER = LocalTime.of(9, 0);
    private static final LocalTime LARK_SLEEP_BEFORE = LocalTime.of(22, 0);
    private static final LocalTime LARK_WAKE_BEFORE = LocalTime.of(7, 0);

    private static final LocalTime NIGHT_WINDOW_START = LocalTime.of(0, 0);
    private static final LocalTime NIGHT_WINDOW_END = LocalTime.of(6, 0);

    @Override
    public String getDescription() {
        return "Хронотип пользователя";
    }

    @Override
    public ChronoType analyze(List<SleepSession> sessions) {
        Map<ChronoType, Long> counts = sessions.stream()
                .filter(ChronotypeFunction::isNightSession)
                .map(ChronotypeFunction::classify)
                .collect(Collectors.groupingBy(type -> type, Collectors.counting()));

        long maxCount = counts.values().stream()
                .max(Long::compareTo)
                .orElse(0L);

        long typesWithMaxCount = counts.values().stream()
                .filter(count -> count == maxCount)
                .count();

        if (maxCount == 0 || typesWithMaxCount > 1) {
            return ChronoType.DOVE;
        }

        return counts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(ChronoType.DOVE);
    }

    private static boolean isNightSession(SleepSession session) {
        LocalDate sessionDate = session.start().toLocalDate();
        return Stream.of(sessionDate.minusDays(1), sessionDate, sessionDate.plusDays(1))
                .anyMatch(night -> overlapsNightWindow(session, night));
    }

    private static boolean overlapsNightWindow(SleepSession session, LocalDate night) {
        LocalDateTime windowStart = LocalDateTime.of(night, NIGHT_WINDOW_START);
        LocalDateTime windowEnd = LocalDateTime.of(night, NIGHT_WINDOW_END);
        return session.start().isBefore(windowEnd) && session.end().isAfter(windowStart);
    }

    private static ChronoType classify(SleepSession session) {
        LocalTime start = session.start().toLocalTime();
        LocalTime end = session.end().toLocalTime();

        if (start.isAfter(OWL_SLEEP_AFTER) && end.isAfter(OWL_WAKE_AFTER)) {
            return ChronoType.OWL;
        }
        if (start.isBefore(LARK_SLEEP_BEFORE) && end.isBefore(LARK_WAKE_BEFORE)) {
            return ChronoType.LARK;
        }
        return ChronoType.DOVE;
    }
}
