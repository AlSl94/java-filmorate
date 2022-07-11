package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(
        value = "/films",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)

public class FilmController {

    private final FilmService filmService;
    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Текущее количество фильмов: {}", filmService.getFilms().size());
        return filmService.findAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film add(@Valid @RequestBody Film filmDto) {
        log.info("Добавлен фильм: {}", filmDto);
        return filmService.add(filmDto);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film update(@Valid @RequestBody Film filmDto) {
        log.info("Фильм обновлен: {}", filmDto);
        return filmService.update(filmDto);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public Film likeFilm(@Valid @PathVariable Long id, Long userId) {
        //TODO log
        return filmService.likeFilm(id, userId);
    }


}
