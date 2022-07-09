package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(
        value = "/films",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)

public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film add(@Valid @RequestBody Film filmDto) {
        if (films.containsKey(filmDto.getId())) {
            throw new ValidationException("Id " + filmDto.getId() + "уже существует. " +
                    "Чтобы внести изменения воспользуйтесь методом PUT");
        }
        if (films.values().stream().anyMatch(f -> f.getName().equals(filmDto.getName())
                && f.getReleaseDate().equals(filmDto.getReleaseDate()))) {
            throw new ValidationException("Фильм с названием " + filmDto.getName()
                    + " и годом выпуска " + filmDto.getReleaseDate() + " уже существует");
        }
        filmDto.setId(++id);
        films.put(filmDto.getId(), filmDto);
        log.info("Добавлен фильм: {}", filmDto);
        return filmDto;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film update(@Valid @RequestBody Film filmDto) {
        if (!films.containsKey(filmDto.getId())) {
            throw new ValidationException(("Id " + filmDto.getId() + " не существует."));
        }
        films.put(filmDto.getId(), filmDto);
        log.info("Фильм обновлен: {}", filmDto);
        return filmDto;
    }
}
