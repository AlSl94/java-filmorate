package ru.yandex.practicum.filmorate.storage.friend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Qualifier("friendDbStorage")
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userStorage;

    @Autowired
    public FriendDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }
    @Override
    public void addFriend(Long id, Long friendId) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }
    @Override
    public void removeFriend(Long id, Long friendId) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", id, friendId);
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", friendId, id);
    }
    @Override
    public Collection<User> getFriends(Long id) {
        List<Long> friendIds = jdbcTemplate.queryForList("SELECT friend_id FROM friends WHERE user_id = ?",
                Long.class, id);
        return friendIds.stream().map(userStorage::findUserById).collect(Collectors.toList());
    }
    @Override
    public Collection<User> commonFriends(Long id, Long friendId) {
        List<Long> commonIds = jdbcTemplate.queryForList("SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID IN (?, ?)"
                , Long.class, id, friendId);
        Set<Long> allIds = new HashSet<>();
        Set<Long> duplicates = commonIds.stream()
                .filter(l -> !allIds.add(l))
                .collect(Collectors.toSet());
        return duplicates.stream().map(userStorage::findUserById).collect(Collectors.toList());
    }

}
