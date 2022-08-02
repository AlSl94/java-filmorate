package ru.yandex.practicum.filmorate.storage.film;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

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
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(id);
        return film;
    }

    @Override
    public Film delete(long id) {
        Film film = jdbcTemplate.queryForObject("SELECT * FROM films WHERE film_id = ?",
                this::mapRowToFilm, id);
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
        return film;
    }

    @Override
    public Film update(@NotNull Film film) {
        jdbcTemplate.update("UPDATE films SET name = ?, description = ?, mpa_rating = ?, " +
                        "duration = ?, release_date = ? WHERE film_id = ?"
                , film.getName()
                , film.getDescription()
                , film.getMpa().getId()
                , film.getDuration()
                , film.getReleaseDate()
                , film.getId());
        Film updatedFilm = jdbcTemplate.queryForObject("SELECT * FROM films WHERE film_id = ?",
                this::mapRowToFilm, film.getId());
        return updatedFilm;
    }

    @Override
    public Film film(Long id) { //TODO
        return jdbcTemplate.queryForObject("SELECT * FROM films WHERE film_id = ?",
                this::mapRowToFilm, id);
    }

    public Mpa mpa(Integer id) {
        return jdbcTemplate.queryForObject("SELECT * FROM mpa_rating WHERE mpa_id = ?",
                this::mapRowToMpa, id);
    }

    public Collection<Mpa> allMpa() {
        return jdbcTemplate.query("SELECT * FROM mpa_rating", this::mapRowToMpa);
    }

    public Genre genre(Integer id) {
        return jdbcTemplate.queryForObject("SELECT * FROM genres WHERE genre_id = ?",
                this::mapRowToGenre, id);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException { //TODO
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .mpa(new Mpa(resultSet.getShort("mpa_rating"), resultSet.getString("mpa")))
                .duration(resultSet.getDouble("duration"))
                .build();
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getShort("mpa_id"))
                .name(resultSet.getString("mpa"))
                .build();
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre"))
                .build();
    }
}
