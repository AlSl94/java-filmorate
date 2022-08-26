package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
@Slf4j
@Service
@Validated
public class FriendService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public FriendService(UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    /**
     * Метод для добавления дружба
     * @param id айди первого пользователя
     * @param friendId айди второго пользователя, которого добавляем в друзья к первому
     */
    public void addFriend(Long id, Long friendId) {
        userStorage.checkUserExistence(id);
        userStorage.checkUserExistence(friendId);
        friendStorage.addFriend(id, friendId);
    }

    /**
     * Метод для удаления друга
     * @param id айди пользователя
     * @param friendId айди друга
     */
    public void removeFriend(Long id, Long friendId) {
        userStorage.checkUserExistence(id);
        userStorage.checkUserExistence(friendId);
        friendStorage.removeFriend(id, friendId);
    }

    /**
     * Метод для получения списка друзей у конкретного пользователя.
     * @param id айди пользователя
     * @return коллекция с друзьями пользователя
     */
    public Collection<User> getFriends(Long id) {
        userStorage.checkUserExistence(id);
        return friendStorage.getFriends(id);
    }

    /**
     * Метод для получения списка общих друзей
     * @param id айди первого пользователя
     * @param friendId айди второго пользователя
     * @return коллекция с общими друзьями двух пользователей
     */
    public Collection<User> commonFriends(Long id, Long friendId) {
        userStorage.checkUserExistence(id);
        userStorage.checkUserExistence(friendId);
        return friendStorage.commonFriends(id, friendId);
    }
}
