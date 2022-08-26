package ru.yandex.practicum.filmorate.storage.mark;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MarkDbStorage implements MarkStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    public MarkDbStorage(JdbcTemplate jdbcTemplate, FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
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
    public Collection<Film> getBestFilms(Integer count, Long genreId, Integer year) {
        List<Long> bestFilmIds;
        if (genreId != null && year != null) {
            final String sqlQuery = "SELECT F.FILM_ID FROM FILMS AS F " +
                    "LEFT JOIN MARKS AS M ON F.FILM_ID = M.FILM_ID " +
                    "LEFT JOIN FILM_GENRE AS FG ON F.FILM_ID = FG.FILM_ID " +
                    "WHERE GENRE_ID = ? AND YEAR(F.RELEASE_DATE) = ? " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY AVG(m.MARK) DESC " +
                    "LIMIT ?";
            bestFilmIds = jdbcTemplate.queryForList(sqlQuery, Long.class, genreId, year, count);
            log.info("Получен топ {} фильмов по по оценкам " +
                    "+ фильтры: id жанра {}, год {}", count, genreId, year);
        } else if (genreId != null) {
            final String sqlQuery = "SELECT F.FILM_ID FROM FILMS AS F " +
                    "LEFT JOIN MARKS AS M ON F.FILM_ID = M.FILM_ID " +
                    "LEFT JOIN FILM_GENRE AS FG ON F.FILM_ID = FG.FILM_ID " +
                    "WHERE GENRE_ID = ? " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY AVG(m.MARK) DESC " +
                    "LIMIT ?";
            bestFilmIds = jdbcTemplate.queryForList(sqlQuery, Long.class, genreId, count);
            log.info("Получен топ {} фильмов по оценкам " +
                    "+ фильтры: id жанра {}", count, genreId);
        } else if (year != null) {
            final String sqlQuery = "SELECT F.FILM_ID FROM FILMS AS F " +
                    "LEFT JOIN MARKS AS M ON F.FILM_ID = M.FILM_ID " +
                    "WHERE YEAR(F.RELEASE_DATE) = ? " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY AVG(m.MARK) DESC " +
                    "LIMIT ?";
            bestFilmIds = jdbcTemplate.queryForList(sqlQuery, Long.class, year, count);
            log.info("Получен топ {} фильмов по оценкам " +
                    "+ фильтры: по году {}", count, year);
        } else {
            final String sqlQuery = "SELECT F.FILM_ID FROM FILMS AS F " +
                    "LEFT JOIN MARKS AS M ON F.FILM_ID = M.FILM_ID " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY AVG(m.MARK) DESC " +
                    "LIMIT ?";
            bestFilmIds = jdbcTemplate.queryForList(sqlQuery, Long.class, count);
            log.info("Получен топ {} фильмов по оценкам, без фильтров", count);
        }

        return bestFilmIds.stream()
                .map(filmStorage::findFilmById)
                .collect(Collectors.toList());
    }

    @Override
    public Double averageFilmRating(Long filmId) {
        final String sqlQuery = "SELECT AVG(MARK) as avg_score " +
                "FROM MARKS " +
                "WHERE FILM_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, Double.class, filmId);
    }
}