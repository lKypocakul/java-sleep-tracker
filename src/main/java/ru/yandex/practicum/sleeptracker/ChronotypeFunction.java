package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Определяет хронотип пользователя ("сова", "жаворонок" или "голубь") на основе
 * анализа времени засыпания и пробуждения по ночным сессиям сна.
 * <p>
 * Алгоритм:
 * <ol>
 *     <li>из всех сессий отбираются только "ночные" — те, что пересекают
 *     окно [00:00, 06:00); дневные сны и бессонные ночи в подсчёте
 *     не участвуют;</li>
 *     <li>каждая ночная сессия классифицируется как "сова", "жаворонок"
 *     или "голубь";</li>
 *     <li>итоговый хронотип — тот, что встречается чаще всего.
 *     При равенстве количеств (или отсутствии ночных сессий) результат —
 *     "голубь".</li>
 * </ol>
 */
public class ChronotypeFunction implements SleepAnalyticsFunction<Chronotype> {

    private static final LocalTime OWL_SLEEP_AFTER = LocalTime.of(23, 0);
    private static final LocalTime OWL_WAKE_AFTER = LocalTime.of(9, 0);
    private static final LocalTime LARK_SLEEP_BEFORE = LocalTime.of(22, 0);
    private static final LocalTime LARK_WAKE_BEFORE = LocalTime.of(7, 0);

    private static final LocalTime NIGHT_WINDOW_START = LocalTime.of(0, 0);
    private static final LocalTime NIGHT_WINDOW_END = LocalTime.of(6, 0);
    private static final String DESCRIPTION = "Хронотип пользователя";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public Chronotype analyze(List<SleepSession> sessions) {
        Map<Chronotype, Long> counts = sessions.stream()
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
            return Chronotype.DOVE;
        }

        return counts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(Chronotype.DOVE);
    }

    /**
     * Сессия считается "ночной", если её интервал пересекает окно [00:00, 06:00)
     * хотя бы на одну из соседних календарных дат.
     */
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

    private static Chronotype classify(SleepSession session) {
        LocalTime start = session.start().toLocalTime();
        LocalTime end = session.end().toLocalTime();

        if (isLaterThanTimeOfNight(start, OWL_SLEEP_AFTER) && isLaterThanTimeOfNight(end, OWL_WAKE_AFTER)) {
            return Chronotype.OWL;
        }
        if (isEarlierThanTimeOfNight(start, LARK_SLEEP_BEFORE) && isEarlierThanTimeOfNight(end, LARK_WAKE_BEFORE)) {
            return Chronotype.LARK;
        }
        return Chronotype.DOVE;
    }

    /**
     * Сравнивает два момента времени суток в рамках одной "ночи", а не одних календарных
     * суток: отсчёт ведётся от полудня, поэтому 01:00 корректно считается "позже", чем 23:00
     * (обычное {@link LocalTime#isAfter} даёт здесь неверный результат из-за перехода через
     * полночь).
     */
    private static boolean isLaterThanTimeOfNight(LocalTime time, LocalTime threshold) {
        return minutesSinceNoon(time) > minutesSinceNoon(threshold);
    }

    private static boolean isEarlierThanTimeOfNight(LocalTime time, LocalTime threshold) {
        return minutesSinceNoon(time) < minutesSinceNoon(threshold);
    }

    private static long minutesSinceNoon(LocalTime time) {
        long noonSeconds = LocalTime.NOON.toSecondOfDay();
        long diffSeconds = Math.floorMod(time.toSecondOfDay() - noonSeconds, 24 * 60 * 60);
        return diffSeconds / 60;
    }
}
