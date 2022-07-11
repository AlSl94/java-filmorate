package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;

@Service
public class UserService {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Getter, который позволяет обращаться напрямую к Мапе из InMemoryUserStorage
     * В первую очередь нужен, чтобы правильно работало логирование в контроллерах
     * @return Map из InMemoryUserStorage
     */
    public Map<Long, User> getUsers() {
        return userStorage.getUsers();
    }

    /**
     * Возвращает весь список пользователей, логика находится в InMemoryUserStorage
     * @return коллекцию всех пользователей
     */
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    /**
     * Метод для создания нового пользователя, основная логика находится в InMemoryUserStorage
     * @param user новый пользователь
     * @return нового пользователя
     */
    public User create(User user) {
        if (getUsers().values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new ValidationException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    /**
     * Метод для обновления пользователя, основная логика находится в InMemoryUserStorage
     * @param user обновленный пользователь
     * @return обновленный пользователь
     */
    public User update(User user) {
        if (!getUsers().containsKey(user.getId())) {
            throw new ValidationException(("Id " + user.getId() + " не существует."));
        }
        return userStorage.update(user);
    }

    /**
     * Находим пользователя по id, логика находится в InMemoryUserStorage, геттер
     * @param id айди пользователя
     * @return пользователь, которого мы нашли по id
     */
    public User getUser(Long id) {
        if (userStorage.getUser(id) == null) {
            throw new ValidationException("Пользователся с " + id + " не существует");
        }
        return userStorage.getUser(id);
    }

    /**
     * Метод для добавления друзей. Логика завязана на HashSet'e.
     * Сет хранится в экземпляре класса User. Сет нужен, чтобы так хранились только уникальные значения.
     * Если пользователь A добавил пользователя B, то и B добавит пользователя A
     * @param id айди первого пользователя
     * @param friendId айди второго пользователя, которого добавляем в друзья к первому
     * @return второго пользователя, которого добавили в друзья
     */
    public User addFriend(Long id, Long friendId) {
        if (userStorage.getUser(id) == null || userStorage.getUser(friendId) == null) {
            throw new ValidationException("DOESNT WORK");
        }
        userStorage.getUser(id).getFriends().add(friendId);
        userStorage.getUser(friendId).getFriends().add(id);
        return userStorage.getUser(friendId);
    }

    /**
     * Метод для удаления друга.
     * @param id айди первого пользователя
     * @param friendId айди второго пользователя
     * @return пользователь, которого мы удалили
     */
    public User removeFriend(Long id, Long friendId) {

        userStorage.getUser(id).getFriends().remove(friendId);
        userStorage.getUser(friendId).getFriends().remove(id);

        return userStorage.getUsers().get(friendId);
    }

    /**
     * Метод для получения списка друзей у конкретного пользователя.
     * Создается новый Set, в который мы добавляем пользователей, находя их в userStorage по айди
     * а затем возвращаем HashSet
     * @param id айди пользователя
     * @return коллекция с друзьями пользователя
     */
    public Collection<User> getFriends(Long id) {
        List<User> users = new ArrayList<>();
        userStorage.getUser(id).getFriends()
                .forEach(i -> users.add(userStorage.getUser(i)));
        return users;
    }

    /**
     * Метод для получения списка общих друзей
     * Логика похожа на метод getFriends()
     * Добавлен метод retainAll, который удаляет все отличающиеся значения
     * @param id айди первого пользователя
     * @param otherId айди второго пользователя
     * @return коллекция с общими друзьями этих двух юзеров
     */
    public Collection<User> commonFriends(Long id, Long otherId) {
        List<User> userList = new ArrayList<>();
        List<User> userList2 = new ArrayList<>();
        userStorage.getUser(id).getFriends().forEach(i -> userList.add(userStorage.getUsers().get(i)));
        userStorage.getUser(otherId).getFriends().forEach(i -> userList2.add(userStorage.getUsers().get(i)));
        userList.retainAll(userList2);
        return userList;
    }
}
