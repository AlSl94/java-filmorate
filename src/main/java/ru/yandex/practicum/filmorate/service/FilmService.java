package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Autowired
    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    /**
     * Метод для получения всех фильмов, основная логика находится в InMemoryFilmStorage
     * @return коллекцию в которой хранятся все фильмы
     */
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    /**
     * Метод для получения фильма по id
     * @param id айди фильма
     * @return фильм
     */
    public Film getFilm(@Valid Long id) {
        if (filmStorage.film(id) == null) {
            throw new WrongParameterException("Фильма с " + id + " не существует");
        }
        return filmStorage.film(id);
    }

    /**
     * Метод для создания нового фильма, основная логика находится в InMemoryFilmStorage
     * @param film новый фильм
     * @return новый фильм
     */
    public Film add(@Valid Film film) {
        final LocalDate OLDEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(OLDEST_RELEASE_DATE)) {
            throw new ValidationException("Дата создания должна быть не раньше 1895-12-28");
        }
        return filmStorage.add(film);
    }

    /**
     * Метод для удаления фильма, логика находится в InMemoryFilmStorage
     * @param id айди фильма
     * @return удаленный фильм
     */
    public Film delete(@Valid Integer id) {
        return filmStorage.delete(id);
    }

    /**
     * Метод для обновления существующего фильма, основная логика находится в InMemoryFilmStorage
     * @param film обновленный фильм
     * @return обновленный фильм
     */
    public Film update(@Valid Film film) {
        if (filmStorage.findAll().stream().noneMatch(f -> Objects.equals(film.getId(), f.getId()))) {
            throw new WrongParameterException("film.id не найден");
        }
        return filmStorage.update(film);
    }

    /**
     * Метод для добавления лайков фильму
     * @param filmId айди фильма
     * @param userId айди пользователя, которое заносим в HashSet
     * @return фильм, который мы нашли по айди
     */
    public Film likeFilm(Long filmId, Long userId) {
        if (userStorage.user(userId) == null) {
            throw new ValidationException("Такого пользователя не существует");
        }
        filmStorage.film(filmId).getUserLikes().add(userId);
        return filmStorage.film(filmId); // TODO
    }

    /**
     * Метод для удаление поставленного лайка фильму
     * @param filmId айди фильма
     * @param userId айди пользователя
     * @return фильм, у которого мы убрали лайк
     */
    public Film unlikeFilm(Long filmId, Long userId) {
        filmStorage.film(filmId).getUserLikes().remove(userId);
        return null; // TODO
    }

    /**
     * Метод для сортировки и получения списка фильмов по количеству лайков
     * @param count какое количество фильмов мы показать пользователю
     * @return List, в котором нужное нам количество фильмов, отсортированных по количеству лайков
     */
    public Collection<Film> filmsByLikeCount(Integer count) {
        List<Film> sortedFilms = filmStorage.findAll().stream()
                .sorted(Comparator.<Film>comparingInt(i -> i.getUserLikes().size()).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
        return sortedFilms.stream()
                .limit(count)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Mpa mpa(Integer mpaId) {
        if (mpaId < 0 || mpaId > 5) {
            throw new WrongParameterException("mpa.id должен быть больше 0 и меньше 5");
        }
        return filmStorage.mpa(mpaId);
    }

    public Collection<Mpa> allMpa() {
        return filmStorage.allMpa();
    }
}