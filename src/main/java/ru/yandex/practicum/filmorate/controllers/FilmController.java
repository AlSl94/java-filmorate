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

    @PostMapping
    public Film add(@Valid @RequestBody Film filmDto) {
        log.info("Добавлен фильм: {}", filmDto);
        return filmService.add(filmDto);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film filmDto) {
        log.info("Фильм обновлен: {}", filmDto);
        return filmService.update(filmDto);
    }

    @GetMapping(value = "/{id}")
    public Film getFilm(@Valid @PathVariable Long id) {
        //TODO logging
        return filmService.getFilm(id);
    }

    @PutMapping(value = "/{filmId}/like/{userId}")
    public Film likeFilm(@Valid @PathVariable Long filmId, @PathVariable Long userId) {
        //TODO logging
        filmService.likeFilm(filmId, userId);
        return filmService.getFilm(filmId);
    }

    @DeleteMapping(value = "/{filmId}/like/{userId}")
    public Integer unlikeFilm(@Valid @PathVariable Long filmId, @PathVariable Long userId) {
        //TODO logging
        filmService.unlikeFilm(filmId, userId);
        return filmService.getFilm(filmId).getUserLikes().size();
    }

    @GetMapping(value = "/popular")
    //TODO logging
    public Collection<Film> filmsByLikesDefault(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.filmsByLikeCount(count);
    }
}
