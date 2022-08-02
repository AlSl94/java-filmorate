package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import javax.validation.Valid;
import java.util.*;
@Slf4j
@Service
@Validated
public class UserService {
    private final UserDbStorage userStorage;

    @Autowired
    public UserService(UserDbStorage userStorage) {
        this.userStorage = userStorage;
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
    public User create(@Valid User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    /**
     * Метод для удаления пользователья, логика находится в InMemoryUserStorage
     * @param id айди пользователя
     * @return удаленного пользователя
     */
    public User delete(@Valid Integer id) {
        return userStorage.delete(id);
    }

    /**
     * Метод для обновления пользователя, основная логика находится в InMemoryUserStorage
     * @param user обновленный пользователь
     * @return обновленный пользователь
     */
    public User update(@Valid User user) {
        if (userStorage.findAll().stream().noneMatch(u -> Objects.equals(user.getId(), u.getId()))) {
            log.info("Попытка обновить пользователя");
            throw new WrongParameterException("user.id не найден");
        }
        return userStorage.update(user);
    }

    /**
     * Находим пользователя по id, логика находится в InMemoryUserStorage, геттер
     * @param id айди пользователя
     * @return пользователь, которого мы нашли по id
     */
    public User getUser(Long id) {
        if (userStorage.findAll().stream().noneMatch(u -> Objects.equals(u.getId(), id))) {
            log.info("Попытка получить пользователя с неверным user.id");
            throw new WrongParameterException("user.id не найден");
        }
        return userStorage.user(id);
    }

    /**
     * Метод для добавления друзей. Используется хешмапа для хранение ключа и статуса дружбы
     * Сет хранится в экземпляре класса User. Сет нужен, чтобы так хранились только уникальные значения.
     * Если пользователь A добавил пользователя B, то и B добавит пользователя A
     * @param id айди первого пользователя
     * @param friendId айди второго пользователя, которого добавляем в друзья к первому
     * @return второго пользователя, которого добавили в друзья
     */
    public User sendFriendRequest(Long id, Long friendId) {
        // Проверка, что такой пользователь существует
        userStorage.user(id).getFriends().put(friendId, false); // По умолчанию дружба не подтверждается
        return userStorage.user(friendId);
    }

    /**
     * Метод для подтверждения дружбы между пользователями
     * @param id id пользователя, который должен принять запрос на дружбу
     * @param friendId id пользователя, который отправил запрос на дружбу
     * @return пользователя, которого приняли в друзья
     */
    public User acceptFriendRequest (Long id, Long friendId) {
        userStorage.user(friendId).getFriends().put(id, true);
        userStorage.user(id).getFriends().put(friendId, true);
        return userStorage.user(id);
    }

    /**
     * Метод для отказа от дружбы с пользователем
     * @param id id пользователя, который отказывается от запроса на дружбу
     * @param friendId id пользователя, который отправил запрос на дружбу
     * @return пользователя, который отказался от дружбы
     */
    public User denyFriendRequest (Long id, Long friendId) {
        userStorage.user(friendId).getFriends().remove(id);
        return userStorage.user(friendId);
    }

    /**
     * Метод для удаления друга.
     * @param id айди первого пользователя
     * @param friendId айди второго пользователя
     * @return пользователь, которого мы удалили
     */
    public User removeFriend(Long id, Long friendId) {
        userStorage.user(id).getFriends().remove(friendId);
        userStorage.user(friendId).getFriends().remove(id);
        return null;
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
        userStorage.user(id).getFriends().keySet()
                .forEach(i -> users.add(userStorage.user(i)));
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
//        checkIfUsersExist(id, otherId);
//        List<User> userList = new ArrayList<>();
//        List<User> userList2 = new ArrayList<>();
//        userStorage.user(id).getFriends().keySet()
//                .forEach(i -> userList.add(userStorage.getUsers().get(i)));
//        userStorage.user(otherId).getFriends().keySet()
//                .forEach(i -> userList2.add(userStorage.getUsers().get(i)));
//        userList.retainAll(userList2);
//        return userList;
        return null;
    }

    /**
     * Проверка, что пользователи существуют
     * @param id id первого пользователя
     * @param friendId id второго пользователя
     */
    private void checkIfUsersExist(Long id, Long friendId) {

    }
}