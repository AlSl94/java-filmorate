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

    @GetMapping(value = "/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable Integer directorId, @RequestParam String sortBy) {
        Collection<Film> films = filmService.getFilmsByDirector(directorId, sortBy);
        log.info("Найден список фильмов по режиссеру {}", directorId);
        return films;
    }

    @GetMapping(value = "/common")
    public Collection<Film> findCommonFilms(@RequestParam Long userId,
                                            @RequestParam Long friendId) {
        log.info("Запрос общих фильмов пользователя c id: {} и друга c id: {}", userId, friendId);
        return filmService.findCommonFilms(userId, friendId);
    }

    @GetMapping(value = "/search")
    public Collection<Film> searchFilm(@RequestParam String query, //Обязательные аннотации query и by
                                       @RequestParam List<String> by) {
        log.info("Поиск фильмов c параметрами запроса query: {} и by: {}", query, by);
        return filmService.searchFilm(query, by);
    }
}