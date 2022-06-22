package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final LocalDate OLDEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Текущее количество фильмов: " + films.size());
        return films.values();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(OLDEST_RELEASE_DATE)) {
            throw new ValidationException("Дата создания должна быть не раньше " + OLDEST_RELEASE_DATE);
        }
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Id " + film.getId() + "уже существует. " +
                    "Чтобы внести изменения воспользуйтесь методом PUT");
        }
        if (films.values().stream().anyMatch(f -> f.getName().equals(film.getName())
                && f.getReleaseDate().equals(film.getReleaseDate()))) {
            throw new ValidationException("Фильм с названием " + film.getName()
                    + " и годом выпуска " + film.getReleaseDate() + " уже существует");
        }
        film.setId(++id);
        films.put(film.getId(), film);
        log.debug("Добавлен фильм: " + film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (film.getReleaseDate().isBefore(OLDEST_RELEASE_DATE)) {
            throw new ValidationException("Дата создания должна быть не раньше " + OLDEST_RELEASE_DATE);
        }
        if (!films.containsKey(film.getId())) {
            throw new ValidationException(("Id " + film.getId() + " не существует."));
        }
        films.put(film.getId(), film);
        log.debug("Фильм обновлен: " + film);
        return film;
    }
}
