package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mark.MarkStorage;

import java.util.Collection;

@Service
@Validated
public class MarkService {

    private final MarkStorage markStorage;

    @Autowired
    public MarkService(MarkStorage markStorage) {
        this.markStorage = markStorage;
    }

    public void scoreFilm(Long filmId, Long userId, Integer mark) {
        markStorage.scoreFilm(filmId, userId, mark);
    }

    public void removeFilmScore(Long filmId, Long userId) {
        markStorage.removeFilmScore(filmId, userId);
    }

    public Collection<Film> getPopularFilms(Integer count, Long genreId, Integer year) {
       return markStorage.getPopularFilms(count, genreId, year);
    }
}
