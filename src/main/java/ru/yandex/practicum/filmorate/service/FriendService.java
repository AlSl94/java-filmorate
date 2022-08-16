package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;
import java.util.Objects;
@Slf4j
@Service
@Validated
public class FriendService {
    private final UserDbStorage userStorage;
    private final FriendDbStorage friendStorage;

    @Autowired
    public FriendService(UserDbStorage userStorage, FriendDbStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    /**
     * Метод для добавления дружба
     * @param id айди первого пользователя
     * @param friendId айди второго пользователя, которого добавляем в друзья к первому
     */
    public void addFriend(Long id, Long friendId) {
        if ((userStorage.findAll().stream().noneMatch(u -> Objects.equals(u.getId(), id)))
                || (userStorage.findAll().stream().noneMatch(u -> Objects.equals(u.getId(), friendId)))) {
            throw new WrongParameterException("user.id или friend.id не найден");
        }
        friendStorage.addFriend(id, friendId);
    }

    /**
     * Метод для удаления друга
     * @param id айди пользователя
     * @param friendId айди друга
     */
    public void removeFriend(Long id, Long friendId) {
        if ((userStorage.findAll().stream().noneMatch(u -> Objects.equals(u.getId(), id)))
                || (userStorage.findAll().stream().noneMatch(u -> Objects.equals(u.getId(), friendId)))) {
            throw new WrongParameterException("user.id или friend.id не найден");
        }
        friendStorage.removeFriend(id, friendId);
    }

    /**
     * Метод для получения списка друзей у конкретного пользователя.
     * @param id айди пользователя
     * @return коллекция с друзьями пользователя
     */
    public Collection<User> getFriends(Long id) {
        if (userStorage.findAll().stream().noneMatch(u -> Objects.equals(u.getId(), id))) {
            log.info("Попытка получить пользователя с неверным user.id");
            throw new WrongParameterException("user.id не найден");
        }
        return friendStorage.getFriends(id);
    }

    /**
     * Метод для получения списка общих друзей
     * @param id айди первого пользователя
     * @param friendId айди второго пользователя
     * @return коллекция с общими друзьями двух пользователей
     */
    public Collection<User> commonFriends(Long id, Long friendId) {
        if ((userStorage.findAll().stream().noneMatch(u -> Objects.equals(u.getId(), id)))
                || (userStorage.findAll().stream().noneMatch(u -> Objects.equals(u.getId(), friendId)))) {
            throw new WrongParameterException("user.id или friend.id не найден");
        }
        return friendStorage.commonFriends(id, friendId);
    }
}
