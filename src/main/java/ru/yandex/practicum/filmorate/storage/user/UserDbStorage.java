package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.utilites.FilmRecommendation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
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
        final String sqlQuery = "SELECT user_id, email, login, name, birthday FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
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
        if (jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id) == 0) {
            throw new WrongParameterException("user.id не найден");
        }
    }

    @Override
    public User update(User user) {
        int updatedRows = jdbcTemplate.update("UPDATE users SET email = ?, login = ?, " +
                "name = ?, birthday = ? WHERE user_id = ?"
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId());
        if (updatedRows == 0) {
            throw new WrongParameterException("user.id не найден");
        }
        return user;
    }

    @Override
    public User findUserById(Long id) {
        final String sqlQuery = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (DataAccessException e) {
            throw new WrongParameterException("user.id не найден");
        }
        return user;
    }

    public Collection<Film> getRecommendations(Long id) {
        List<Long> usersWithSimilarInterestsIds = getUsersWithSimilarInterests(id);
        if (usersWithSimilarInterestsIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Double> targetUserRates = getUsersRates(Collections.singletonList(id)).get(id);
        Map<Long, Map<Long, Double>> similarUsersRates = getUsersRates(usersWithSimilarInterestsIds);

        List<Long> recommendation = FilmRecommendation.getRecommendation(targetUserRates, similarUsersRates);
        return recommendation.stream().map(filmStorage::findFilmById).collect(Collectors.toList());
    }

    @Override
    public void checkUserExistence(Long id) {
        final String sqlQuery = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (DataAccessException e) {
            throw new WrongParameterException("user.id или friend.id не найден");
        }
    }

    @Override
    public void checkUserExistence(Long id, Long friendId) {
        final String sqlQuery = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
            jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, friendId);
        } catch (DataAccessException e) {
            throw new WrongParameterException("user.id или friend.id не найден");
        }
    }

    private List<Long> getUsersWithSimilarInterests(Long id) {
        final String sqlQuery = "SELECT DISTINCT m2.user_id FROM marks AS m2" +
                " JOIN MARKS m1 ON m1.film_id = m2.film_id" +
                " WHERE m1.user_id = ?1 AND m2.user_id <> ?1";

        return jdbcTemplate.queryForList(sqlQuery, Long.class, id);
    }

    private Map<Long, Map<Long, Double>> getUsersRates(List<Long> usersIds) {
        String ids = usersIds.stream().map(Object::toString).collect(Collectors.joining(", "));
        String sqlQuery = String.format("SELECT user_id, film_id, mark FROM MARKS" +
                " WHERE user_id IN (%s)", ids);
        Map<Long, Map<Long, Double>> usersRates = new HashMap<>();
        jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToUsersRates(rs, usersRates));
        return usersRates;
    }

    private long mapRowToUsersRates(ResultSet resultSet,Map<Long, Map<Long, Double>> mapToStock)
            throws SQLException {
        Long userId = resultSet.getLong("user_id");
        Long filmId = resultSet.getLong("film_id");
        Double mark = resultSet.getDouble("mark");
        mapToStock.putIfAbsent(userId, new HashMap<>());
        mapToStock.get(userId).put(filmId, mark);
        return userId;
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
