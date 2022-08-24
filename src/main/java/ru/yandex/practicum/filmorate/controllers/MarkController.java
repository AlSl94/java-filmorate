package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.MarkService;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(
        value = "/films",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class MarkController {

    private final MarkService markService;

    @Autowired
    public MarkController(MarkService markService) {
        this.markService = markService;
    }

    @PutMapping(value = "/{filmId}/score/{userId}/{mark}")
    public void scoreFilm(@PathVariable Long filmId, @PathVariable Long userId, @PathVariable Integer mark) {
        markService.scoreFilm(filmId, userId, mark);
        log.info("Поставлена оценка {} фильму с id: {}, от пользователя с id: {}", mark, filmId, userId);
    }

    @DeleteMapping(value = "/{filmId}/score/{userId}")
    public void removeFilmScore(@PathVariable Long filmId, @PathVariable Long userId) {
        markService.removeFilmScore(filmId, userId);
        log.info("Удалена оценка у фильма с id: {}, от пользователя c id: {}", filmId, userId);
    }

    @GetMapping(value = "/popular")
    public Collection<Film> getBestFilms(@RequestParam(defaultValue = "10", required = false) Integer count,
                                         @RequestParam(required = false) Long genreId,
                                         @RequestParam(required = false) Integer year) {
        Collection<Film> topFilms = markService.getBestFilms(count, genreId, year);
        log.info("Получен топ {} фильмов по средней оценке", count);
        return topFilms;
    }
}
