package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, DirectorStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
    }

    @Override
    public List<Film> findAll() {
        final String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.MPA_ID, MR.MPA as MPA, f.RELEASE_DATE, " +
                "f.DURATION " +
                "FROM FILMS AS f " +
                "JOIN MPA_RATING AS MR on MR.MPA_ID = f.MPA_ID ";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        films.forEach(f -> f.setDirectors(directorStorage.directorsByFilm(f.getId())));
        films.forEach(f -> f.setGenres(loadFilmGenre(f.getId())));
        return films;
    }

    @Override
    public Film add(Film film) {
        final String sqlQuery = "INSERT INTO FILMS (NAME, DESCRIPTION, MPA_ID, DURATION, RELEASE_DATE) " +
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
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        try {
            if (!film.getGenres().isEmpty()) {
                final String sqlGenreQuery = "MERGE INTO film_genre VALUES (?, ?)";
                film.getGenres()
                        .forEach(genre -> jdbcTemplate.update(sqlGenreQuery, film.getId(), genre.getId()));
            }
            if (!film.getDirectors().isEmpty()) {
                final String sqlDirectorQuery = "MERGE INTO film_director VALUES (?, ?)";
                film.getDirectors()
                        .forEach(director -> jdbcTemplate.update(sqlDirectorQuery, film.getId(), director.getId()));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return findFilmById(film.getId());
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
    }

    @Override
    public Film update(@NotNull Film film) {
        final String sqlQuery = "UPDATE films SET name = ?, description = ?, mpa_id = ?, " +
                "duration = ?, release_date = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getMpa().getId()
                , film.getDuration()
                , film.getReleaseDate()
                , film.getId());

        try {
            if (!film.getGenres().isEmpty()) { // Проверяем, что жанры не пустые
                final String sqlDeleteQuery = "DELETE FROM film_genre WHERE film_id = ?";
                jdbcTemplate.update(sqlDeleteQuery, film.getId());

                final String mergeSqlQuery = "MERGE INTO film_genre (film_id, genre_id) values (?, ?)";
                film.getGenres()
                        .forEach(genre -> jdbcTemplate.update(mergeSqlQuery, film.getId(), genre.getId()));
            } else { // В противном случае удаляем все из таблицы
                final String sqlDeleteQuery = "DELETE FROM film_genre WHERE film_id = ?";
                jdbcTemplate.update(sqlDeleteQuery, film.getId());
            }

            if (!film.getDirectors().isEmpty()) { // Проверяем, что лист режиссеров не пустой
                final String sqlDeleteQuery = "DELETE FROM film_director WHERE film_id = ?";
                jdbcTemplate.update(sqlDeleteQuery, film.getId());

                final String sqlMergeQuery = "MERGE INTO film_director (film_id, director_id) values (?, ?)";
                film.getDirectors()
                        .forEach(director -> jdbcTemplate.update(sqlMergeQuery, film.getId(), director.getId()));
            } else { // В противном случае удаляем всех режиссеров из таблицы
                final String sqlDeleteQuery = "DELETE FROM film_director WHERE film_id = ?";
                jdbcTemplate.update(sqlDeleteQuery, film.getId());
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return findFilmById(film.getId());
    }

    @Override
    public Film findFilmById(Long id) {

        final String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.MPA_ID, mr.MPA as MPA, " +
                "f.RELEASE_DATE, f.DURATION " +
                "FROM FILMS AS f " +
                "JOIN MPA_RATING AS mr on mr.MPA_ID = f.MPA_ID " +
                "WHERE f.FILM_ID = ?";

        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        assert film != null;
        film.setGenres(loadFilmGenre(id));
        film.setDirectors(directorStorage.directorsByFilm(id));
        return film;
    }

    public List<Film> getFilmsByDirector(Integer id, String sortBy) {
        switch (sortBy) {
            case "year":
                final String sqlQueryByYear =
                        "SELECT f.film_id, f.name, f.description, f.mpa_id, mr.mpa, f.release_date, f.duration " +
                                "FROM FILMS AS f " +
                                "JOIN MPA_RATING AS mr on mr.MPA_ID = f.MPA_ID " +
                                "INNER JOIN film_director AS fd on f.film_id = fd.film_id " +
                                "WHERE fd.director_id = ? " +
                                "ORDER BY f.RELEASE_DATE";
                List<Film> filmsByYear = jdbcTemplate.query(sqlQueryByYear, this::mapRowToFilm, id);
                filmsByYear.forEach(f -> f.setDirectors(directorStorage.directorsByFilm(f.getId())));
                filmsByYear.forEach(f -> f.setGenres(loadFilmGenre(f.getId())));
                return filmsByYear;
            case "likes":
                final String sqlQueryByLikes = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.MPA_ID as MPA_ID, " +
                        "mr.MPA, f.DURATION, f.RELEASE_DATE " +
                        "FROM FILMS as f " +
                        "JOIN MPA_RATING mr on mr.MPA_ID = f.MPA_ID " +
                        "LEFT JOIN likes l on f.film_id = l.film_id " +
                        "LEFT JOIN film_director fd on f.FILM_ID = fd.FILM_ID " +
                        "WHERE fd.DIRECTOR_ID = ? " +
                        "GROUP BY f.FILM_ID " +
                        "ORDER BY COUNT(l.USER_ID) DESC";
                List<Film> filmsByLikes = jdbcTemplate.query(sqlQueryByLikes, this::mapRowToFilm, id);
                filmsByLikes.forEach(f -> f.setDirectors(directorStorage.directorsByFilm(f.getId())));
                filmsByLikes.forEach(f -> f.setGenres(loadFilmGenre(f.getId())));
                return filmsByLikes;
            default:
                return new ArrayList<>(1);
        }
    }

    @Override
    public List<Long> getUsersFilmsIds(List<Long> usersIds) {
        String ids = usersIds.stream().map(Object::toString).collect(Collectors.joining(", "));
        String sqlQuery = String.format("SELECT DISTINCT film_id FROM likes WHERE user_id IN (%s)", ids);
        return jdbcTemplate.queryForList(sqlQuery, Long.class);
    }

    @Override
    public List<Film> searchFilm(String query, List<String> by) {
        List<Film> searchedFilms;
        if (by.contains("director") && by.contains("title")) {
            final String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.MPA_ID as MPA_ID, " +
                    "mr.MPA, f.DURATION, f.RELEASE_DATE " +
                    "FROM films AS f " +
                    "JOIN MPA_RATING AS MR on MR.MPA_ID = f.MPA_ID " +
                    "LEFT JOIN FILM_DIRECTOR FD on f.FILM_ID = FD.FILM_ID " +
                    "LEFT JOIN DIRECTORS D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                    "LEFT JOIN LIKES L on f.FILM_ID = L.FILM_ID " +
                    "WHERE f.NAME ilike '%' || ? || '%' OR d.DIRECTOR_NAME ilike '%' || ? || '%' " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(l.USER_ID) DESC";

            searchedFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, query, query);

        } else if (by.contains("director")) {
            final String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.MPA_ID as MPA_ID, " +
                    "mr.MPA, f.DURATION, f.RELEASE_DATE " +
                    "FROM films as f " +
                    "JOIN MPA_RATING MR on MR.MPA_ID = f.MPA_ID " +
                    "JOIN FILM_DIRECTOR FD on f.FILM_ID = FD.FILM_ID " +
                    "JOIN DIRECTORS D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                    "LEFT JOIN LIKES L on f.FILM_ID = L.FILM_ID " +
                    "WHERE d.DIRECTOR_NAME ilike '%' || ? || '%' " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(l.USER_ID) DESC";

            searchedFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, query);

        } else { //Тогда поиск по названию
            final String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.MPA_ID as MPA_ID, " +
                    "mr.MPA, f.DURATION, f.RELEASE_DATE " +
                    "FROM films as f " +
                    "JOIN MPA_RATING MR on MR.MPA_ID = f.MPA_ID " +
                    "LEFT JOIN LIKES L on f.FILM_ID = L.FILM_ID " +
                    "WHERE f.NAME ilike '%' || ? || '%' " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(l.USER_ID) DESC";

            searchedFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, query);
        }

        searchedFilms.forEach(f -> f.setGenres(loadFilmGenre(f.getId())));
        searchedFilms.forEach(f -> f.setDirectors(directorStorage.directorsByFilm(f.getId())));
        return searchedFilms;
    }

    public List<Film> findCommonFilms(Long userId, Long friendId) {
        final String sqlQuery = "SELECT FILM_ID, COUNT(USER_ID) " +
                "FROM LIKES " +
                "WHERE FILM_ID IN " +
                "(SELECT L1.FILM_ID FROM LIKES AS L1 JOIN LIKES AS L2 ON L2.FILM_ID = L1.FILM_ID " +
                "WHERE L1.USER_ID = ? AND L2.USER_ID = ?) " +
                "GROUP BY FILM_ID " +
                "ORDER BY COUNT(USER_ID) DESC";
        List<Long> ids = jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> rs.getLong("FILM_ID"), userId, friendId);
        return ids.stream().map(this::findFilmById).collect(Collectors.toList());
    }

    private List<Genre> loadFilmGenre(Long id) {
        List<Integer> genreIds = jdbcTemplate.queryForList("SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?",
                Integer.class, id);
        return genreIds.stream().map(genreStorage::getGenreById).collect(Collectors.toList());
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