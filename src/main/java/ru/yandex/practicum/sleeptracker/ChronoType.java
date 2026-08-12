package ru.yandex.practicum.sleeptracker;

public enum ChronoType {
    OWL("Сова"),
    LARK("Жаворонок"),
    DOVE("Голубь");

    private final String displayName;

    ChronoType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
