package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.utilites.FilmRecommendation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

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
        if (usersWithSimilarInterestsIds.size() == 0) {
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
