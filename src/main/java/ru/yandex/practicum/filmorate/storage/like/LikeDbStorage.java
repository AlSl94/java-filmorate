package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

    public LikeDbStorage(JdbcTemplate jdbcTemplate,
                         FilmStorage filmStorage,
                         EventStorage eventStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
        this.eventStorage = eventStorage;
    }

    @Override
    public void likeFilm(Long filmId, Long userId) {
        String sqlQuery = "MERGE INTO likes (film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        eventStorage.createEvent(userId, filmId, 1, 1);
    }

    @Override
    public void unlikeFilm(Long filmId, Long userId) {
        if (!checkLikePair(filmId, userId)) {
            throw new WrongParameterException("такой пары filmId-userId не существует");
        }
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        eventStorage.createEvent(userId, filmId, 1, 3);
    }

    @Override
    public List<Film> getPopularFilms(Integer count, Long genreId, Integer year) {
        List<Long> popularFilmIds;
        if (genreId != null && year != null) {
            final String sqlQuery = "SELECT F.FILM_ID FROM FILMS AS F " +
                    "LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                    "LEFT JOIN FILM_GENRE AS FG ON F.FILM_ID = FG.FILM_ID " +
                    "WHERE GENRE_ID = ? AND YEAR(F.RELEASE_DATE) = ? " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY COUNT(L.USER_ID) DESC " +
                    "LIMIT ?";
            popularFilmIds = jdbcTemplate.queryForList(sqlQuery, Long.class, genreId, year, count);
            log.info("Получен топ {} фильмов по количеству лайков " +
                    "+ фильтры: id жанра {}, год {}", count, genreId, year);
        } else if (genreId != null) {
            final String sqlQuery = "SELECT F.FILM_ID FROM FILMS AS F " +
                    "LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                    "LEFT JOIN FILM_GENRE AS FG ON F.FILM_ID = FG.FILM_ID " +
                    "WHERE GENRE_ID = ? " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY COUNT(L.USER_ID) DESC " +
                    "LIMIT ?";
            popularFilmIds = jdbcTemplate.queryForList(sqlQuery, Long.class, genreId, count);
            log.info("Получен топ {} фильмов по количеству лайков " +
                    "+ фильтры: id жанра {}", count, genreId);
        } else if (year != null) {
            final String sqlQuery = "SELECT F.FILM_ID FROM FILMS AS F " +
                    "LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                    "WHERE YEAR(F.RELEASE_DATE) = ? " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY COUNT(L.USER_ID) DESC " +
                    "LIMIT ?";
            popularFilmIds = jdbcTemplate.queryForList(sqlQuery, Long.class, year, count);
            log.info("Получен топ {} фильмов по количеству лайков " +
                    "+ фильтры: по году {}", count, year);
        } else {
            final String sqlQuery = "SELECT F.FILM_ID FROM FILMS AS F " +
                    "LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY COUNT(L.USER_ID) DESC " +
                    "LIMIT ?";
            popularFilmIds = jdbcTemplate.queryForList(sqlQuery, Long.class, count);
            log.info("Получен топ {} фильмов по количеству лайков, без фильтров", count);
        }

        return popularFilmIds.stream()
                .map(filmStorage::findFilmById)
                .collect(Collectors.toList());
    }

    private boolean checkLikePair(Long filmId, Long userId) {
        String sqlQuery = "SELECT EXISTS(SELECT * FROM likes WHERE film_id = ? AND user_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, filmId, userId));
    }

    public Collection<Long> findCommonFilmsId(Long userId, Long friendId) {
        String sqlQuery = "SELECT FILM_ID, COUNT(USER_ID) " +
                "FROM LIKES " +
                "WHERE FILM_ID IN " +
                "(SELECT L1.FILM_ID FROM LIKES AS L1 JOIN LIKES AS L2 ON L2.FILM_ID = L1.FILM_ID " +
                "WHERE L1.USER_ID = ? AND L2.USER_ID = ?) " +
                "GROUP BY FILM_ID " +
                "ORDER BY COUNT(USER_ID) DESC";
        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> rs.getLong("FILM_ID"), userId, friendId);
    }
}