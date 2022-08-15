package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
@Service
@Validated
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final DirectorDbStorage directorStorage;

    @Autowired
    public FilmService(FilmDbStorage filmStorage, DirectorDbStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.directorStorage = directorStorage;
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
    public Film findFilmById(@Valid Long id) {
        if (filmStorage.findAll().stream().noneMatch(f -> Objects.equals(f.getId(), id))) {
            throw new WrongParameterException("Фильма с " + id + " не существует");
        }
        return filmStorage.findFilmById(id);
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
    public void delete(@Valid Long id) {
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

    public Collection<Film> getFilmsByDirector(Integer id, String sortBy) {
        if (directorStorage.getAllDirectors().stream().noneMatch(d -> Objects.equals(id, d.getId()))) {
            throw new WrongParameterException("director.id не найден");
        }
        return filmStorage.getFilmsByDirector(id, sortBy);
    }

}