package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class SleepTrackerApp {

    private static final List<SleepAnalyticsFunction<?>> ANALYTICS_FUNCTIONS = List.of(
            new TotalSessionsCountFunction(),
            new MinSessionDurationFunction(),
            new MaxSessionDurationFunction(),
            new AverageSessionDurationFunction(),
            new BadQualitySessionsCountFunction(),
            new SleeplessNightsCountFunction(),
            new ChronotypeFunction()
    );

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        if (args.length < 1) {
            System.out.println("Использование: java SleepTrackerApp <путь к файлу с логом сна>");
            return;
        }

        List<SleepSession> sessions;
        try {
            sessions = SleepLogParser.parse(Path.of(args[0]));
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл с логом сна: " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("Файл с логом сна повреждён или имеет неверный формат: " + e.getMessage());
            return;
        }

        if (sessions.isEmpty()) {
            System.out.println("В файле не найдено ни одной сессии сна.");
            return;
        }

        System.out.println("Анализ сна пользователя");
        System.out.println("=========================================================");

        ANALYTICS_FUNCTIONS.forEach(function ->
                System.out.println(function.getDescription() + ": " + function.analyze(sessions))
        );
    }
}
