package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.LikeService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(
        value = "/films",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class LikeController {

    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PutMapping(value = "/{filmId}/like/{userId}")
    public void likeFilm(@PathVariable Long filmId, @PathVariable Long userId) {
        likeService.likeFilm(filmId, userId);
        log.info("Поставлен лайку фильм с id: {}, от пользователя с id: {}", filmId, userId);
    }

    @DeleteMapping(value = "/{filmId}/like/{userId}")
    public void unlikeFilm(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Удален лайк у фильма с id: {}, от пользователя c id: {}", filmId, userId);
        likeService.unlikeFilm(filmId, userId);
    }

    @GetMapping(value = "/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count,
                                            @RequestParam(required = false) Long genreId,
                                            @RequestParam(required = false) Integer year) {
        Collection<Film> topFilms = likeService.getPopularFilms(count, genreId, year);
        log.info("Получен топ {} фильмов по количеству лайков", count);
        return topFilms;
    }
}
