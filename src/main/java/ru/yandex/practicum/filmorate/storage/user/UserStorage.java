package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();

    User create(User user);

    void delete(Long id);

    User update(User user);

    User findUserById(Long id);

    Collection<Film> getRecommendations(Long id);

    void checkUserExistence(Long id);

    void checkUserExistence(Long id, Long friendId);
}