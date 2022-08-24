package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

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

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Long id) {
        filmService.delete(id);
        log.info("Удален фильм c id: {}", id);
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

    @GetMapping(value = "/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable Integer directorId, @RequestParam String sortBy) {
        Collection<Film> films = filmService.getFilmsByDirector(directorId, sortBy);
        log.info("Выдан список фильмов по id режиссера {}", directorId);
        return films;
    }

    @GetMapping(value = "/common")
    public Collection<Film> findCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        Collection<Film> films = filmService.findCommonFilms(userId, friendId);
        log.info("Выдан список общих фильмов пользователя c id: {} и друга c id: {}", userId, friendId);
        return films;
    }

    @GetMapping(value = "/search")
    public Collection<Film> searchFilm(@RequestParam String query, @RequestParam List<String> by) {
        Collection<Film> films = filmService.searchFilm(query, by);
        log.info("Выдан список фильмов c параметрами запроса query: {} и by: {}", query, by);
        return films;
    }
}