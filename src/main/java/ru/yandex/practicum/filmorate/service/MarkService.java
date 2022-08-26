package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mark.MarkStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
public class MarkService {

    private final MarkStorage markStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;


    @Autowired
    public MarkService(MarkStorage markStorage, UserStorage userStorage, FilmStorage filmStorage) {
        this.markStorage = markStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public void scoreFilm(Long filmId, Long userId, Integer mark) {
        if (mark < 1 || mark > 10) {
            throw new WrongParameterException("mark может быть от 1 до 10");
        }
        filmStorage.checkFilmExistence(filmId);
        userStorage.checkUserExistence(userId);
        markStorage.scoreFilm(filmId, userId, mark);
    }

    public void removeFilmScore(Long filmId, Long userId) {
        filmStorage.checkFilmExistence(filmId);
        userStorage.checkUserExistence(userId);
        markStorage.removeFilmScore(filmId, userId);
    }

    public Collection<Film> getBestFilms(Integer count, Long genreId, Integer year) {
       return markStorage.getBestFilms(count, genreId, year);
    }
}