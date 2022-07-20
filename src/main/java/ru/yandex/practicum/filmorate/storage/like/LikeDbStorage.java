package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.List;
import java.util.stream.Collectors;


@Component
@Qualifier("likeDbStorage")
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmStorage;

    public LikeDbStorage(JdbcTemplate jdbcTemplate, FilmDbStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;

        this.filmStorage = filmStorage;
    }

    @Override
    public void likeFilm (Long filmId, Long userId) {
        String sqlQuery = "MERGE INTO likes (film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }
    @Override
    public void unlikeFilm(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }
    @Override
    public List<Film> getPopularFilms(Integer count) { // Вот этот метод пограничный, использует 2 таблицы из БД
        String sqlQuery = "SELECT f.film_id " + // Поэтому не знаю, где его использовать, в film или like, решил, что
                "FROM FILMS AS f " + // будет здесь
                "LEFT JOIN LIKES l on f.FILM_ID = l.FILM_ID " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.USER_ID) " +
                "DESC LIMIT ?";
        List<Long> popularFilmIds = jdbcTemplate.queryForList(sqlQuery, Long.class, count);
        return popularFilmIds.stream().map(filmStorage::findFilmById).collect(Collectors.toList());
    }

    public boolean checkLikePair(Long filmId, Long userId) {
        String sqlQuery = "SELECT EXISTS(SELECT * FROM likes WHERE film_id = ? AND user_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, filmId, userId));
    }
}
