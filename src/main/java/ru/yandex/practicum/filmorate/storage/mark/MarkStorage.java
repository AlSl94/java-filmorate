package ru.yandex.practicum.filmorate.storage.mark;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface MarkStorage {
    Double MARK_AT_WHICH_POSITIVE_RATING_STARTS = 6.0;

    void scoreFilm(Long filmId, Long userId, Integer mark);

    void removeFilmScore(Long filmId, Long userId);

    Collection<Film> getBestFilms(Integer count, Long genreId, Integer year);

    Double averageFilmRating(Long id);
}
