package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage{
    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
    }

    @Override
    public Collection<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", this::mapRowToUser);
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        long id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(id);
        return user;
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id);
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update("UPDATE users SET email = ?, login = ?, " +
                "name = ?, birthday = ? WHERE user_id = ?"
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId());
        return user;
    }

    @Override
    public User findUserById(Long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id = ?",
                this::mapRowToUser, id);
    }

    public Collection<Film> getRecommendations(Long id) {
        List<Long> usersWithSimilarInterestsIds = getUsersWithSimilarInterests(id);
        List<Long> targetUserRatedFilmsIds = filmStorage.getUsersFilmsIds(Collections.singletonList(id));
        List<Long> similarFilmsIds = filmStorage.getUsersFilmsIds(usersWithSimilarInterestsIds);
        List<Long> similarFilmsThatNotWatchedIds = similarFilmsIds.stream()
                .filter(a -> !targetUserRatedFilmsIds.contains(a))
                .collect(Collectors.toList());
        Map<Long, Double> targetUserRates = new HashMap<>();
        for (Long filmId : similarFilmsIds) {
            targetUserRates.put(filmId, targetUserRatedFilmsIds.contains(filmId) ? 1.0 : 0.0);
        }

        Map<Long, Double> usersCorrelations = new HashMap<>();
        Map<Long, Map<Long, Double>> similarUsersFilmsRates = new HashMap<>();
        for (Long userId : usersWithSimilarInterestsIds) {
            HashMap<Long, Double> userRates = new HashMap<>();
            List<Long> userFilmsIds = filmStorage.getUsersFilmsIds(Collections.singletonList(userId));
            for (Long filmId : similarFilmsIds) {
                userRates.put(filmId, userFilmsIds.contains(filmId) ? 1.0 : 0.0);
            }
            similarUsersFilmsRates.put(userId, userRates);
            usersCorrelations.put(userId, calculateCorrelation(targetUserRates, similarUsersFilmsRates.get(userId)));
        }

        List<Long> ratedFilms = getCorrelatedRecommendations(similarFilmsThatNotWatchedIds,
                similarUsersFilmsRates, usersCorrelations);

        return ratedFilms.stream().map(filmStorage::findFilmById).collect(Collectors.toList());
    }

    private Double calculateCorrelation(Map<Long, Double> firstUserRates, Map<Long, Double> secondUserRates) {
        int count = 0;
        double sum1 = 0.0;
        double sum2 = 0.0;
        double pSum = 0.0;
        double sqSum1 = 0.0;
        double sqSum2 = 0.0;
        for (Long id : firstUserRates.keySet()) {
            if (secondUserRates.containsKey(id)) {
                count++;
                sum1 += firstUserRates.get(id);
                sum2 += secondUserRates.get(id);
                pSum += firstUserRates.get(id) * secondUserRates.get(id);
                sqSum1 += pow(firstUserRates.get(id), 2);
                sqSum2 += pow(secondUserRates.get(id), 2);
            }
        }
        double num = (pSum - (sum1 * sum2 / count));
        double den = (sqrt(sqSum1 - pow(sum1, 2) / count) * sqrt(sqSum2 - pow(sum2, 2) / count));

        if (den == 0.0) {
            return 0.0;
        }
        return num / den;
    }

    private List<Long> getCorrelatedRecommendations(List<Long> filmsIds, Map<Long, Map<Long, Double>> usersFilmRates,
                                                    Map<Long, Double> usersCorrelations) {
        Map<Long, Double> correlatedFilms = new HashMap<>();
        Map<Long, Double> sumCorrelations = new HashMap<>();

        for (Long userId : usersFilmRates.keySet()) {
            for (Long filmId : filmsIds) {
                if (usersFilmRates.get(userId).get(filmId) > 0) {
                    double correlatedUserRate =
                            usersFilmRates.get(userId).get(filmId) * usersCorrelations.get(userId);
                    correlatedFilms.put(filmId, correlatedFilms.getOrDefault(filmId, 0.0) + correlatedUserRate);
                    sumCorrelations.put(filmId,
                            sumCorrelations.getOrDefault(filmId, 0.0) + usersCorrelations.get(userId));
                }
            }
        }
        for (Long filmId : correlatedFilms.keySet()) {
            correlatedFilms.put(filmId, correlatedFilms.get(filmId) / sumCorrelations.get(filmId));
        }

        return correlatedFilms.keySet().stream()
                .sorted(Comparator.comparingDouble(correlatedFilms::get).reversed())
                .collect(Collectors.toList());
    }

    private List<Long> getUsersWithSimilarInterests(Long id) {
        String sqlQuery = "SELECT DISTINCT l2.user_id FROM likes AS l2" +
                " JOIN likes AS l1 on l1.film_id = l2.film_id" +
                " WHERE l1.user_id = ?1 AND l2.user_id <> ?1";

        return jdbcTemplate.queryForList(sqlQuery, Long.class, id);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
