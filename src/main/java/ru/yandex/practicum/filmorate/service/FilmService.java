package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    /**
     * Getter для доступа к Map<Film> films в InMemoryFilmStorage
     * Нужен, в первую очередь, для логгирования в контроллере
     * @return мапа со всеми фильмами
     */
    public Map<Long, Film> getFilms() {
        return filmStorage.getFilms();
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
    public Film getFilm(Long id) {
        if (filmStorage.getFilm(id) == null) {
            throw new ValidationException("Фильма с " + id + " не существует");
        }
        return filmStorage.getFilm(id);
    }

    /**
     * Метод для создания нового фильма, основная логика находится в InMemoryFilmStorage
     * @param film новый фильм
     * @return новый фильм
     */
    public Film add(Film film) {
        final LocalDate OLDEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
        if (getFilms().containsKey(film.getId())) {
            throw new ValidationException("Id " + film.getId() + "уже существует. " +
                    "Чтобы внести изменения воспользуйтесь методом PUT");
        }
        if (getFilms().values().stream().anyMatch(f -> f.getName().equals(film.getName())
                && f.getReleaseDate().equals(film.getReleaseDate()))) {
            throw new ValidationException("Фильм с названием " + film.getName()
                    + " и годом выпуска " + film.getReleaseDate() + " уже существует");
        }
        if (film.getReleaseDate().isBefore(OLDEST_RELEASE_DATE)) {
            throw new WrongParameterException("Дата создания должна быть не раньше 1895-12-28");
        }
        return filmStorage.add(film);
    }

    /**
     * Метод для обновления существующего фильма, основная логика находится в InMemoryFilmStorage
     * @param film обновленный фильм
     * @return обновленный фильм
     */
    public Film update(Film film) {
        if (!getFilms().containsKey(film.getId())) {
            throw new ValidationException(("Id " + film.getId() + " не существует."));
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
        if (userStorage.getUser(userId) == null) {
            throw new ValidationException("Такого пользователя не существует");
        }
        filmStorage.getFilm(filmId).getUserLikes().add(userId);
        return filmStorage.getFilms().get(filmId);
    }

    /**
     * Метод для удаление поставленного лайка фильму
     * @param filmId айди фильма
     * @param userId айди пользователя
     * @return фильм, у которого мы убрали лайк
     */
    public Film unlikeFilm(Long filmId, Long userId) {
        if (!filmStorage.getFilm(filmId).getUserLikes().contains(userId)) {
            throw new ValidationException("Пользователся с " + userId + " не существует");
        }
        filmStorage.getFilm(filmId).getUserLikes().remove(userId);
        return filmStorage.getFilms().get(filmId);
    }

    /**
     * Метод для сортировки и получения списка фильмов по количеству лайков
     * @param count какое количество фильмов мы показать пользователю
     * @return List, в котором нужное нам количество фильмов, отсортированных по количеству лайков
     */
    public Collection<Film> filmsByLikeCount(Integer count) {

        List<Film> sortedFilms = filmStorage.getFilms().values().stream()
                .sorted(Comparator.<Film>comparingInt(i -> i.getUserLikes().size()).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        return sortedFilms.stream()
                .limit(count)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}