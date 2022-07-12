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
        log.info("Текущее количество фильмов: {}", filmService.getFilms().size());
        return filmService.findAll();
    }

    @PostMapping
    public Film add(@RequestBody Film filmDto) {
        log.info("Добавлен фильм: {}", filmDto);
        return filmService.add(filmDto);
    }

    @DeleteMapping
    public Film delete(@RequestBody Integer id) {
        log.info("Удален фильм c id: {}", id);
        return filmService.delete(id);
    }

    @PutMapping
    public Film update(@RequestBody Film filmDto) {
        log.info("Фильм обновлен: {}", filmDto);
        return filmService.update(filmDto);
    }

    @GetMapping(value = "/{id}")
    public Film getFilm(@PathVariable Long id) {
        log.info("Найден фильм по id: {}", filmService.getFilm(id));
        return filmService.getFilm(id);
    }

    @PutMapping(value = "/{filmId}/like/{userId}")
    public Film likeFilm(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Поставлен лайку фильм с id: {}, от пользователя с id: {}", filmId, userId);
        filmService.likeFilm(filmId, userId);
        return filmService.getFilm(filmId);
    }

    @DeleteMapping(value = "/{filmId}/like/{userId}")
    public Integer unlikeFilm(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Удален лайк у фильма с id: {}, от пользователя c id: {}", filmId, userId);
        filmService.unlikeFilm(filmId, userId);
        return filmService.getFilm(filmId).getUserLikes().size();
    }

    @GetMapping(value = "/popular")
    public Collection<Film> filmsByLikesDefault(@RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("Получен топ {} фильмов по количеству лайков", count);
        return filmService.filmsByLikeCount(count);
    }
}