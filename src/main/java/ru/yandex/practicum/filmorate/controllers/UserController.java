package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;
    @GetMapping
    public Collection<User> findAll() {
        log.info("Текущее количество пользователей: " + users.size());
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта должна содержать знак '@'");
        }
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new ValidationException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Создан пользователь: " + user);
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта должна содержать знак '@'");
        }
        if (!users.containsKey(user.getId())) {
            throw new ValidationException(("Id " + user.getId() + " не существует."));
        }
        log.info("Обновлен пользователь: " + user);
        users.put(user.getId(), user);
        return user;
    }
}