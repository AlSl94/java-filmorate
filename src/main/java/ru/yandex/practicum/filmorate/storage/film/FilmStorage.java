package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film add(Film film);

    void delete(Long id);

    Film update(Film film);

    Film findFilmById(Long id);
}