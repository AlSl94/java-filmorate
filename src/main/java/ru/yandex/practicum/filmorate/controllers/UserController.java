package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
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
    public User getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
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
        log.info("Обновлен пользователь: {}", userDto);
        return userService.update(userDto);
    }
    @DeleteMapping
    public void delete(@RequestBody Integer id) {
        log.info("Удален пользователь c id: {}", id);
        userService.delete(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с id {} отправил запрос на дружбу пользователю с id {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
        log.info("Пользователи с id {} и с id {} перестали быть друзьями", id, friendId);
    }

    @GetMapping(value = "/{id}/friends") //todo
    public Collection<User> getFriends(@PathVariable Long id) {
        Collection<User> friends = userService.getFriends(id);
        log.info("Получен список друзей у пользователя с id {}", id);
        return friends;
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}") //todo
    public Collection<User> commonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        Collection<User> commonFriends = userService.commonFriends(id, otherId);
        log.info("Получен список общих друзей у пользователей с id {} и {}", id, otherId);
        return commonFriends;
    }
}