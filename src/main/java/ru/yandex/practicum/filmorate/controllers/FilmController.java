package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final int MAX_LENGTH = 200;
    private final LocalDate OLDEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Текущее количество фильмов: " + films.size());
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        if (film.getName().isBlank() || film.getName() == null) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > MAX_LENGTH) {
            throw new ValidationException("Максимальная длина не может превышать" + MAX_LENGTH + "символов");
        }
        if (film.getReleaseDate().isBefore(OLDEST_RELEASE_DATE)) {
            throw new ValidationException("Дата создания должна быть не раньше " + OLDEST_RELEASE_DATE);
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть больше 0");
        }
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: " + film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film.getName().isBlank() || film.getName() == null) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getName().length() > MAX_LENGTH) {
            throw new ValidationException("Максимальная длина не может превышать" + MAX_LENGTH + "символов");
        }
        if (film.getReleaseDate().isBefore(OLDEST_RELEASE_DATE)) {
            throw new ValidationException("Дата создания должна быть не раньше " + OLDEST_RELEASE_DATE);
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть больше 0");
        }
        if (!films.containsKey(film.getId())) {
            throw new ValidationException(("Id " + film.getId() + " не существует."));
        }
        films.put(film.getId(), film);
        log.info("Фильм обновлен: " + film);
        return film;
    }
}
