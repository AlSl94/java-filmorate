package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikeStorage {
    void likeFilm (Long filmId, Long userId);

    void unlikeFilm(Long filmId, Long userId);

    List<Film> getPopularFilms(Integer count);
}
