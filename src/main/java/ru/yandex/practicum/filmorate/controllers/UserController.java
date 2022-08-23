package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(
        value = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)

public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        Collection<User> users = userService.findAll();
        log.info("Текущее количество пользователей: {}", users);
        return users;
    }

    @GetMapping(value = "/{id}")
    public User findUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        log.info("Получен пользователь с id: {}", id);
        return user;
    }

    @PostMapping
    public User create(@RequestBody User userDto) {
        User user = userService.create(userDto);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User userDto) {
        User user = userService.update(userDto);
        log.info("Обновлен пользователь: {}", userDto);
        return user;
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
        log.info("Удален пользователь c id: {}", id);
    }

    @GetMapping(value = "/{id}/feed")
    public Collection<Event> findFeedByUserId(@PathVariable(name = "id") Long userId) {
        Collection<Event> feed = userService.findFeedByUserId(userId);
        log.info("Получена лента пользователя с id: {}", userId);
        return feed;
    }

    @GetMapping(value = "/{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable Long id) {
        Collection<Film> films = userService.getRecommendations(id);
        log.info("Выданы рекомендации для пользователя с id: {}", id);
        return films;
    }
}