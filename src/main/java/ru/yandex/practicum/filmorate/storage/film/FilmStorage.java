package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film add(Film film);

    void delete(Long id);

    Film update(Film film);

    Film findFilmById(Long id);

    List<Long> getUsersFilmsIds(List<Long> usersIds);

    List<Film> searchFilm(String query, List<String> by);

    List<Film> getFilmsByDirector(Integer id, String sortBy);
}