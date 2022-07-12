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
        log.info("Текущее количество пользователей: {}", userService.getUsers().size());
        return userService.findAll();
    }

    @GetMapping(value = "/{id}")
    public User getUser(@PathVariable Long id) {
        log.info("Получен пользователь с id: {}", id);
        return userService.getUser(id);
    }

    @PostMapping
    public User create(@RequestBody User userDto) {
        log.info("Создан пользователь: {}", userDto);
        return userService.create(userDto);
    }

    @PutMapping
    public User update(@RequestBody User userDto) {
        log.info("Обновлен пользователь: {}", userDto);
        return userService.update(userDto);
    }
    @DeleteMapping
    public User delete(@RequestBody Integer id) {
        log.info("Удален пользователь c id: {}", id);
        return userService.delete(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId){
        log.info("Пользователи с id {} и с id {} стали друзьями", id, friendId);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователи с id {} и с id {} перестали быть друзьями", id, friendId);
        return userService.removeFriend(id, friendId);
    }

    @GetMapping(value = "/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        log.info("Получен список друзей у пользователя с id {}", id);
        return userService.getFriends(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен список общих друзей у пользователей с id {} и {}", id, otherId);
        return userService.commonFriends(id, otherId);
    }
}