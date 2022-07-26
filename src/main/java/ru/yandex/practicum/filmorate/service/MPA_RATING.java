package ru.yandex.practicum.filmorate.service;

public enum MPA_RATING {

    UNRATED (""),
    G ("G"),
    PG ("PG"),
    PG13 ("PG-13"),
    R ("R"),
    NC17 ("NC-17");

    MPA_RATING(String rating) {
        this.rating = rating;
    }
    private final String rating;
    @Override
    public String toString() {
        return rating;
    }
}
