package ru.yandex.practicum.sleeptracker;

/**
 * Хронотип пользователя на основе анализа времени засыпания и пробуждения.
 */
public enum Chronotype {
    OWL("Сова"),
    LARK("Жаворонок"),
    DOVE("Голубь");

    private final String displayName;

    Chronotype(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
