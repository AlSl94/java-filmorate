package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreStorage;
    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.MPA_ID, MR.MPA as MPA, " +
                "f.RELEASE_DATE, f.DURATION, fg.GENRE_ID AS GENRE_ID, g.GENRE AS GENRE " +
                "FROM FILMS AS f " +
                "JOIN MPA_RATING MR on MR.MPA_ID = f.MPA_ID " +
                "LEFT JOIN FILM_GENRE AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRES AS g ON g.GENRE_ID = fg.GENRE_ID ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film add(Film film) {
        String sqlQuery = "INSERT INTO FILMS (NAME, DESCRIPTION, MPA_ID, DURATION, RELEASE_DATE) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setShort(3, film.getMpa().getId());
            stmt.setDouble(4, film.getDuration());
            if (film.getReleaseDate() == null) {
                stmt.setNull(5, Types.DATE);
            } else {
                stmt.setDate(5, Date.valueOf(film.getReleaseDate()));
            }
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());

        if (!film.getGenres().isEmpty()) {
            String sqlGenreQuery = "MERGE INTO film_genre VALUES (?, ?)";
            film.getGenres().forEach(genre -> jdbcTemplate.update(sqlGenreQuery, film.getId(), genre.getId()));
        }
        return findFilmById(film.getId());
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
    }

    @Override
    public Film update(@NotNull Film film) {
        jdbcTemplate.update("UPDATE films SET name = ?, description = ?, mpa_id = ?, " +
                        "duration = ?, release_date = ? WHERE film_id = ?"
                , film.getName()
                , film.getDescription()
                , film.getMpa().getId()
                , film.getDuration()
                , film.getReleaseDate()
                , film.getId());

            if (!film.getGenres().isEmpty()) {
                String sqlDeleteQuery = "DELETE FROM film_genre WHERE film_id = ?";
                jdbcTemplate.update(sqlDeleteQuery, film.getId());

                String sqlQuery = "MERGE INTO FILM_GENRE (film_id, genre_id) values (?, ?)";
                IntStream.range(0, film.getGenres().size()).forEach(i -> jdbcTemplate.update(sqlQuery,
                        film.getId(),
                        film.getGenres().get(i).getId()));
            } else {
                String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
                jdbcTemplate.update(sqlQuery, film.getId());
            }
        return findFilmById(film.getId());
    }
    @Override
    public Film findFilmById(Long id) {
        String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.MPA_ID, MR.MPA as MPA, " +
                "f.RELEASE_DATE, f.DURATION " +
                "FROM FILMS AS f " +
                "JOIN MPA_RATING MR on MR.MPA_ID = f.MPA_ID " +
                "WHERE f.FILM_ID = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery,
                this::mapRowToFilm, id);
        List<Genre> genres = genreStorage.loadFilmGenre(id);
        if (film != null) film.setGenres(genres);
        return film;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
            return Film.builder()
                    .id(rs.getLong("film_id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .mpa(new Mpa(rs.getShort("mpa_id"), rs.getString("mpa")))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getDouble("duration"))
                    .build();
    }
}
