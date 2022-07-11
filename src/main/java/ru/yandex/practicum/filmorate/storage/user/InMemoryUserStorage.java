package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage{
    @Getter
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    /**
     * Возвращает всех пользователей
     * @return значения мапы users
     */
    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    /**
     * Создает нового пользователя
     * @param user новый пользователь
     * @return нового пользователя
     */
    @Override
    public User create(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Обновляет существующего пользователя
     * @param user обновленный пользователь
     * @return обновленного пользователя
     */
    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Возвращает конкретного пользователя по id
     * @param id айди пользователя
     * @return пользователя, которого мы нашли по id
     */
    @Override
    public User getUser(Integer id) {
        return users.get(id);
    }
}
