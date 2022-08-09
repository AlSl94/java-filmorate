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
    public void delete(@Valid Long id) {
        if (userStorage.findAll().stream().noneMatch(u -> Objects.equals(id, u.getId()))) {
            log.info("Попытка удалить пользователя");
            throw new WrongParameterException("user.id не найден");
        }
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
    public User findUserById(Long id) {
        if (userStorage.findAll().stream().noneMatch(u -> Objects.equals(u.getId(), id))) {
            log.info("Попытка получить пользователя с неверным user.id");
            throw new WrongParameterException("user.id не найден");
        }
        return userStorage.findUserById(id);
    }
}