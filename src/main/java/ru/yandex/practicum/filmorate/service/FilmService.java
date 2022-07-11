package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    /**
     * Getter для доступа к Map<Film> films в InMemoryFilmStorage
     * Нужен, в первую очередь, для логгирования в контроллере
     * @return мапа со всеми фильмами
     */
    public Map<Integer, Film> getFilms() {
        return filmStorage.getFilms();
    }

    /**
     * Метод для получения всех фильмов, основная логика находится в InMemoryFilmStorage
     * @return коллекцию в которой хранятся все фильмы
     */
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getFilm(Long id) {
        return filmStorage.getFilm(id);
    }

    /**
     * Метод для создания нового фильма, основная логика находится в InMemoryFilmStorage
     * @param film новый фильм
     * @return новый фильм
     */
    public Film add(Film film) {
        if (getFilms().containsKey(film.getId())) {
            throw new ValidationException("Id " + film.getId() + "уже существует. " +
                    "Чтобы внести изменения воспользуйтесь методом PUT");
        }
        if (getFilms().values().stream().anyMatch(f -> f.getName().equals(film.getName())
                && f.getReleaseDate().equals(film.getReleaseDate()))) {
            throw new ValidationException("Фильм с названием " + film.getName()
                    + " и годом выпуска " + film.getReleaseDate() + " уже существует");
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
        filmStorage.getFilm(filmId).getUserLikes().remove(userId);
        return filmStorage.getFilms().get(filmId);
    }

    /**
     * Метод для сортировки и получения списка фильмов по количеству лайков
     * @param count какое количество фильмов мы показать пользователю
     * @return HashSet, в котором нужное нам количество фильмов, отсортированных по количеству лайков
     */
    public Set<Film> filmsByLikeCount(Integer count) {
        Set<Film> films = new HashSet<>();
        filmStorage.getFilms().keySet()
                .forEach(i -> films.add(filmStorage.getFilms().get(i)));
        if (count == 0) {
            count = 10;
        }
        Set<Film> sortedFilms = films.stream()
                .sorted(Comparator.comparingInt(i -> i.getUserLikes().size()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return sortedFilms.stream().limit(count).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}