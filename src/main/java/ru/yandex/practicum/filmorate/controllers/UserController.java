package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User create(@Valid @RequestBody User userDto) {
        log.info("Создан пользователь: {}", userDto);
        return userService.create(userDto);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User update(@Valid @RequestBody User userDto) {
        log.info("Обновлен пользователь: {}", userDto);
        return userService.update(userDto);
    }

    @PutMapping(value = "/{id}/friends/{friendId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public User addFriend(@Valid @PathVariable Integer id, @PathVariable Integer friendId){
        //TODO Logging
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public User deleteFriend(@Valid @PathVariable Integer id, @PathVariable Integer friendId) {
        //TODO Logging
        return userService.removeFriend(id, friendId);
    }

    @GetMapping(value = "/{id}/friends",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Collection<User> getFriends(@Valid @PathVariable Integer id) {
        //TODO logging
        return userService.getFriends(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Collection<User> getCommonFriends(@Valid @PathVariable Integer id, @PathVariable Integer otherId) {
        //TODO logging
        return userService.commonFriends(id, otherId);
    }

    @GetMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public User getUser(@Valid @PathVariable Integer id) {
        //TODO logging
        return userService.getUser(id);
    }
}