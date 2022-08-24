package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film add(Film film);

    void delete(Long id);

    Film update(Film film);

    Film findFilmById(Long id);

    List<Film> searchFilm(String query, List<String> by);

    Collection<Film> findCommonFilms(Long userId, Long friendId);

    List<Film> getFilmsByDirector(Integer id, String sortBy);

    void checkFilmExistence(Long id);
}