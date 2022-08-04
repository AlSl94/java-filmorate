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
    public void delete(@RequestBody Integer id) {
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
    public Film getFilm(@PathVariable Long id) {
        Film film = filmService.getFilm(id);
        log.info("Найден фильм по id: {}", film);
        return film;
    }

    @PutMapping(value = "/{filmId}/like/{userId}")
    public void likeFilm(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.likeFilm(filmId, userId);
        log.info("Поставлен лайку фильм с id: {}, от пользователя с id: {}", filmId, userId);
    }

    @DeleteMapping(value = "/{filmId}/like/{userId}")
    public void unlikeFilm(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Удален лайк у фильма с id: {}, от пользователя c id: {}", filmId, userId);
        filmService.unlikeFilm(filmId, userId);
    }

    @GetMapping(value = "/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        Collection<Film> topFilms = filmService.getPopularFilms(count);
        log.info("Получен топ {} фильмов по количеству лайков", count);
        return topFilms;
    }
}