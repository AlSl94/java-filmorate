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
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

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
     * Удаляет пользователя
     * @param id айди пользователя
     * @return удаленного пользователя
     */
    @Override
    public User delete(long id) {
        return users.remove(id);
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
    public User user(long id) {
        return users.get(id);
    }
}