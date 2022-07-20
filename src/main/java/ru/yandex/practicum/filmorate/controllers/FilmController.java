package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

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
        Collection<Film> films = filmService.findAll();
        log.info("Текущее количество фильмов: {}", films.size());
        return films;
    }

    @PostMapping
    public Film add(@RequestBody Film filmDto) {
        filmService.add(filmDto);
        log.info("Добавлен фильм: {}", filmDto);
        return filmDto;
    }

    @DeleteMapping
    public void delete(@RequestBody Long id) {
        log.info("Удален фильм c id: {}", id);
        filmService.delete(id);
    }

    @PutMapping
    public Film update(@RequestBody Film filmDto) {
        Film film = filmService.update(filmDto);
        log.info("Фильм обновлен: {}", filmDto);
        return film;
    }

    @GetMapping(value = "/{id}")
    public Film findFilmById(@PathVariable Long id) {
        Film film = filmService.findFilmById(id);
        log.info("Найден фильм по id: {}", film);
        return film;
    }
}