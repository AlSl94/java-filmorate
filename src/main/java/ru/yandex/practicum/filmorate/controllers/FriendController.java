package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(
        value = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class FriendController {

    private final FriendService friendService;

    @Autowired
    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с id {} отправил запрос на дружбу пользователю с id {}", id, friendId);
        friendService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        friendService.removeFriend(id, friendId);
        log.info("Пользователи с id {} и с id {} перестали быть друзьями", id, friendId);
    }

    @GetMapping(value = "/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        Collection<User> friends = friendService.getFriends(id);
        log.info("Получен список друзей у пользователя с id {}", id);
        return friends;
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public Collection<User> commonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        Collection<User> commonFriends = friendService.commonFriends(id, otherId);
        log.info("Получен список общих друзей у пользователей с id {} и {}", id, otherId);
        return commonFriends;
    }
}