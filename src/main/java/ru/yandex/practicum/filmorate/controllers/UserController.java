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
        log.info("Обновлен пользователь: {}", userDto);
        return userService.update(userDto);
    }
    @DeleteMapping
    public void delete(@RequestBody Long id) {
        log.info("Удален пользователь c id: {}", id);
        userService.delete(id);
    }
}