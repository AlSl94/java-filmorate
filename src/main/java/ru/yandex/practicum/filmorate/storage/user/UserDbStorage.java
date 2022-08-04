package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage{

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
    public void delete(long id) {
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
    public User user(long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id = ?",
                this::mapRowToUser, id);
    }
    public void addFriend(Long id, Long friendId) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }
    public void removeFriend(Long id, Long friendId) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", id, friendId);
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", friendId, id);
    }
    public Collection<User> getFriends(Long id) {
        List<Long> friendIds = jdbcTemplate.queryForList("SELECT friend_id FROM friends WHERE user_id = ?",
                Long.class, id);
        return friendIds.stream().map(this::user).collect(Collectors.toList());
    }

    public Collection<User> commonFriends(Long id, Long friendId) {
        List<Long> commonIds = jdbcTemplate.queryForList("SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID IN (?, ?)"
                , Long.class, id, friendId);
        Set<Long> allIds = new HashSet<>();
        Set<Long> duplicates = commonIds.stream()
                .filter(l -> !allIds.add(l))
                .collect(Collectors.toSet());
        return duplicates.stream().map(this::user).collect(Collectors.toList());
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
