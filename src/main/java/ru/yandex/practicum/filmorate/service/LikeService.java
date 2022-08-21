package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.util.Collection;

@Service
@Validated
public class LikeService {

    private final LikeStorage likeStorage;

    @Autowired
    public LikeService(LikeStorage likeStorage) {
        this.likeStorage = likeStorage;
    }

    /**
     * Метод для добавления лайков фильму
     * @param filmId айди фильма
     * @param userId айди пользователя
     */
    public void likeFilm(Long filmId, Long userId) {
        likeStorage.likeFilm(filmId, userId);
    }

    /**
     * Метод для удаления поставленного лайка фильму.
     * Есть валидация методом checkLikePair из класса FilmDbStorage
     * @param filmId айди фильма
     * @param userId айди пользователя
     */
    public void unlikeFilm(Long filmId, Long userId) {
        likeStorage.unlikeFilm(filmId, userId);
    }

    /**
     * Метод для получения списка фильмов по количеству лайков с возможностью применения фильтров по жанру и году
     * @param count - какое количество фильмов мы хотим показать
     * @param genreId - по какому жанру хотим отфильтровать результат (опционально)
     * @param year - по какому году хотим отфильтровать результат (опционально)
     * @return List, в котором нужное нам количество фильмов,
     * отсортированных по количеству лайков и (опционально) отфильтрованных по условиям
     */
    public Collection<Film> getPopularFilms(Integer count, Long genreId, Integer year) {
        return likeStorage.getPopularFilms(count, genreId, year);
    }
}
