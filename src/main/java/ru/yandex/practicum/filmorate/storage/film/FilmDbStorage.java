package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.MPA_RATING;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage{

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> findAll() {
        return jdbcTemplate.query("SELECT * FROM films", this::mapRowToFilm);
    }

    @Override
    public Film add(Film film) {
        Map<String, Object> parameters = new HashMap<>(5);

        jdbcTemplate.update("INSERT INTO films (name, description, rating, duration, " +
                        "release_date) " + "values (?, ?, ?, ?, ?)", film.fromFilm(film));

        Long id = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id")
                .executeAndReturnKey(film.toMap()).longValue();

        jdbcTemplate.update("INSERT INTO FILM_GENRE (film_id, genre)"
                + "values (?, ?)", id, film.getGenres());
        return film;
    }

    @Override
    public Film delete(long id) {
        Film film = jdbcTemplate.queryForObject("SELECT * FROM films WHERE film_id = ?",
                Film.class, id);
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
        return film;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update("UPDATE films SET film_id = ?, name = ?, description = ?, " +
                        "rating = ?, duration = ?, release_date = ?", film.fromFilm(film));
        return film;
    }

    @Override
    public Film film(Long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM films WHERE film_id = ?",
                Film.class, id);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .filmId(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .rating(MPA_RATING.valueOf(resultSet.getString("rating")))
                .duration(resultSet.getDouble("duration"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .build();
    }
}
