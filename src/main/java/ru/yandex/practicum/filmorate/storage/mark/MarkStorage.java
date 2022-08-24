package ru.yandex.practicum.filmorate.storage.mark;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface MarkStorage {

    void scoreFilm(Long filmId, Long userId, Integer mark);

    void removeFilmScore(Long filmId, Long userId);

    Collection<Film> getPopularFilms(Integer count, Long genreId, Integer year);
}
