package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage{

    @Override
    public Collection<User> findAll() {
        return null;
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User delete(long id) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public User user(long id) {
        return null;
    }
}
