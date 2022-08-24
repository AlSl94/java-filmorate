package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@Validated
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, DirectorStorage directorStorage) {
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
        return filmStorage.findFilmById(id);
    }

    /**
     * Метод для создания нового фильма
     * @param film новый фильм
     * @return новый фильм
     */
    public Film add(@Valid Film film) {
        final LocalDate oldestReleaseDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(oldestReleaseDate)) {
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
        return filmStorage.update(film);
    }

    public Collection<Film> getFilmsByDirector(Integer id, String sortBy) {
        if (directorStorage.getAllDirectors().stream().noneMatch(d -> Objects.equals(id, d.getId()))) {
            throw new WrongParameterException("director.id не найден");
        }
        return filmStorage.getFilmsByDirector(id, sortBy);
    }

    /**
     * Метод для вывода общих с другом фильмов с сортировкой по их популярности
     *
     * @param userId   - идентификатор пользователя, запрашивающего информацию
     * @param friendId - идентификатор пользователя, с которым необходимо сравнить список фильмов
     * @return - Возвращает список фильмов, отсортированных по популярности.
     */
    public Collection<Film> findCommonFilms(Long userId, Long friendId) {
        return filmStorage.findCommonFilms(userId, friendId);
    }
    /**
     * Метод для поиска фильма
     *
     * @param query искомые символы
     * @param by    выбор, где искать
     * @return коллекция найденных фильмов
     */
    public Collection<Film> searchFilm(String query, List<String> by) {
        if (query.length() < 3) throw new WrongParameterException("Количество символов запроса поиска меньше 3-х");
        if (query.isBlank()) throw new WrongParameterException("Строка запроса поиска пустая");
        if (!by.contains("director") && !by.contains("title"))
            throw new WrongParameterException("Параметр поиска указан не верно");

        return filmStorage.searchFilm(query, by);
    }
}