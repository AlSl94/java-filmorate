package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@Validated
public class UserService {
    private final UserStorage userStorage;
    private final EventStorage eventStorage;


    @Autowired
    public UserService(UserStorage userStorage, EventStorage eventStorage) {
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    /**
     * Возвращает весь список пользователей
     *
     * @return коллекцию всех пользователей
     */
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    /**
     * Метод для создания нового пользователя
     *
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
     *
     * @param id айди пользователя
     */
    public void delete(@Valid Long id) {
        userStorage.delete(id);
        log.info("Пользователь с id: {} удален.", id);
    }

    /**
     * Метод для обновления пользователя
     *
     * @param user обновленный пользователь
     * @return обновленный пользователь
     */
    public User update(@Valid User user) {
        user = userStorage.update(user);
        log.info("Обновлен пользователь с id: {}", user.getId());
        return user;
    }

    /**
     * Находим пользователя по id, логика находится в InMemoryUserStorage, геттер
     *
     * @param id айди пользователя
     * @return пользователь, которого мы нашли по id
     */
    public User findUserById(Long id) {
        User user = userStorage.findUserById(id);
        log.info("Получен пользователь с id: {}", id);
        return user;
    }

    /**
     * Находим ленту пользователя по его id в хранилище событий
     *
     * @param userId айди пользователя
     * @return лента (коллекция событий) пользователя, которую мы нашли по его id
     */
    public Collection<Event> findFeedByUserId(Long userId) {
        Collection<Event> events = eventStorage.getFeedByUserId(userId);
        log.info("Получены события пользователя с id: {}", userId);
        return events;
    }

    /**
     * Метод для получения рекомендаций с помощью алгоритма slope one
     * @param id айди пользователя
     * @return коллекция фильмов
     */
    public Collection<Film> getRecommendations(Long id) {
        return userStorage.getRecommendations(id);
    }
}