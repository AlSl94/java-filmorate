package ru.yandex.practicum.filmorate.storage.mark;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Component
public class MarkDbStorage implements MarkStorage {

    private final JdbcTemplate jdbcTemplate;

    public MarkDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void scoreFilm(Long filmId, Long userId, Integer mark) {
        final String sqlQuery = "MERGE INTO MARKS VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId, mark);
    }

    @Override
    public void removeFilmScore(Long filmId, Long userId) {
        final String sqlQuery = "DELETE FROM MARKS WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count, Long genreId, Integer year) {
        return new ArrayList<>();
    }

}
