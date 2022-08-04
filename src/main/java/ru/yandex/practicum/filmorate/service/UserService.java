package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Objects;
@Slf4j
@Service
@Validated
public class UserService {
    private final UserDbStorage userStorage;

    @Autowired
    public UserService(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Возвращает весь список пользователей
     * @return коллекцию всех пользователей
     */
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    /**
     * Метод для создания нового пользователя
     * @param user новый пользователь
     * @return нового пользователя
     */
    public User create(@Valid User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    /**
     * Метод для удаления пользователя
     * @param id айди пользователя
     */
    public void delete(@Valid Integer id) {
       userStorage.delete(id);
    }

    /**
     * Метод для обновления пользователя
     * @param user обновленный пользователь
     * @return обновленный пользователь
     */
    public User update(@Valid User user) {
        if (userStorage.findAll().stream().noneMatch(u -> Objects.equals(user.getId(), u.getId()))) {
            log.info("Попытка обновить пользователя");
            throw new WrongParameterException("user.id не найден");
        }
        return userStorage.update(user);
    }

    /**
     * Находим пользователя по id, логика находится в InMemoryUserStorage, геттер
     * @param id айди пользователя
     * @return пользователь, которого мы нашли по id
     */
    public User getUser(Long id) {
        if (userStorage.findAll().stream().noneMatch(u -> Objects.equals(u.getId(), id))) {
            log.info("Попытка получить пользователя с неверным user.id");
            throw new WrongParameterException("user.id не найден");
        }
        return userStorage.user(id);
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
        userStorage.addFriend(id, friendId);
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
        userStorage.removeFriend(id, friendId);
    }

    /**
     * Метод для получения списка друзей у конкретного пользователя.
     * @param id айди пользователя
     * @return коллекция с друзьями пользователя
     */
    public Collection<User> getFriends(Long id) {
        return userStorage.getFriends(id);
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
        return userStorage.commonFriends(id, friendId);
    }
}