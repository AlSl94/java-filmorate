package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

@Service
@Validated
public class FilmService {
    private final FilmDbStorage filmStorage;

    @Autowired
    public FilmService(FilmDbStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    /**
     * Метод для получения всех фильмов
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
        if (filmStorage.findAll().stream().noneMatch(f -> Objects.equals(f.getId(), id))) {
            throw new WrongParameterException("Фильма с " + id + " не существует");
        }
        return filmStorage.film(id);
    }

    /**
     * Метод для создания нового фильма
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
     * Метод для удаления фильма
     * @param id айди фильма
     */
    public void delete(@Valid Integer id) {
        filmStorage.delete(id);
    }

    /**
     * Метод для обновления существующего фильма
     * @param film обновленный фильм
     * @return обновленный фильм, который пропустили через метод filmStorage.film()
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
     * @param userId айди пользователя
     */
    public void likeFilm(Long filmId, Long userId) {
        filmStorage.likeFilm(filmId, userId);
    }

    /**
     * Метод для удаление поставленного лайка фильму
     * Есть валидация методом checkLikePair из класса FilmDbStorage
     * @param filmId айди фильма
     * @param userId айди пользователя
     */
    public void unlikeFilm(Long filmId, Long userId) {
        if (!filmStorage.checkLikePair(filmId, userId)) {
            throw new WrongParameterException("такой пары filmId-userId не сущетвует");
        }
        filmStorage.unlikeFilm(filmId, userId);
    }

    /**
     * Метод для получения списка фильмов по количеству лайков
     * @param count - какое количество фильмов мы хотим показать
     * @return List, в котором нужное нам количество фильмов, отсортированных по количеству лайков
     */
    public Collection<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }
}