package ru.yandex.practicum.filmorate.storage.friend;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Repository
public interface FriendStorage {

    void addFriend(Long id, Long friendId);
    void removeFriend(Long id, Long friendId);
    Collection<User> getFriends(Long id);
    Collection<User> commonFriends(Long id, Long friendId);
}
