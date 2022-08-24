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
            // Если пересекающихся пользователей нет, то дальше и считать нечего, выдаем пустой список.
            return Collections.emptyList();
        }

        List<Long> targetUserRatedFilmsIds = filmStorage.getUsersFilmsIds(Collections.singletonList(id));
        List<Long> similarFilmsIds = filmStorage.getUsersFilmsIds(usersWithSimilarInterestsIds);

        // Заполняем таблицу оценок целевого пользователя
        Map<Long, Double> targetUserRates = new HashMap<>();
        similarFilmsIds.stream().filter(targetUserRatedFilmsIds::contains).forEach(l -> targetUserRates.put(l, 1.0));

        // Заполняем таблицу оценок пользователей со схожими вкусами
        Map<Long, Map<Long, Double>> similarUsersFilmsRates = new HashMap<>();
        for (Long userId : usersWithSimilarInterestsIds) {
            HashMap<Long, Double> userRates = new HashMap<>();
            List<Long> userRatedFilmsIds = filmStorage.getUsersFilmsIds(Collections.singletonList(userId));
            similarFilmsIds.stream().filter(userRatedFilmsIds::contains).forEach(l -> userRates.put(l, 1.0));
            similarUsersFilmsRates.put(userId, userRates);
        }

        List<Long> recommendation = FilmRecommendation.getRecommendation(targetUserRates, similarUsersFilmsRates);
        return recommendation.stream().map(filmStorage::findFilmById).collect(Collectors.toList());
    }

    public void checkUserExistence(Long id) {
        final String sqlQuery = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (DataAccessException e) {
            throw new WrongParameterException("user.id или friend.id не найден");
        }
    }

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
        final String sqlQuery = "SELECT DISTINCT l2.user_id FROM likes AS l2" +
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
