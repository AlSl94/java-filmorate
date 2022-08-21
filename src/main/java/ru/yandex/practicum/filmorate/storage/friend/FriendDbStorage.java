package ru.yandex.practicum.filmorate.storage.friend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Autowired
    public FriendDbStorage(JdbcTemplate jdbcTemplate,
                           UserStorage userStorage,
                           EventStorage eventStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }
    @Override
    public void addFriend(Long id, Long friendId) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
        eventStorage.createEvent(id, friendId, 2, 1);

    }
    @Override
    public void removeFriend(Long id, Long friendId) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", id, friendId);
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", friendId, id);
        eventStorage.createEvent(id, friendId, 2, 3);

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