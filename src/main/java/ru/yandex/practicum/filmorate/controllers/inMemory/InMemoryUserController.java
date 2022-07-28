package ru.yandex.practicum.filmorate.controllers.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
/*@RestController
@RequestMapping(
        value = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)*/

public class InMemoryUserController {
    private final UserService userService;
    @Autowired
    public InMemoryUserController(UserService userService) {
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

    @PostMapping(value = "/{id}/friends/{friendId}")
    public User sendFriendRequest(@PathVariable Long id, @PathVariable Long friendId){
        log.info("Пользователь с id {} отправил запрос на дружбу пользователю с id {}", id, friendId);
        return userService.sendFriendRequest(id, friendId);
    }

    @PutMapping(value = "/{id}/friends/request/{friendId}")
    public User acceptFriendRequest(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с id {} принял запрос на дружбу от пользователя с id {}", id, friendId);
        return userService.acceptFriendRequest(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/request/{friendId}")
    public User denyFriendRequest(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с id {} отказал на запрос на дружбу от пользователя с id {}", id, friendId);
        return userService.denyFriendRequest(id, friendId);
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