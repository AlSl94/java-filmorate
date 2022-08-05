package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {

    private final UserService userService;

//     User userOne = new User("test@email.ru", "TestNameOne", "TestLoginOne",
//            LocalDate.of(1994, 2, 10));
//    User userTwo = new User("test2@email.ru", "TestNameTwo", "TestLoginTwo",
//            LocalDate.of(2000, 2, 22));
//    @Test
//    void create() {
//        userService.create(userOne);
//
//        assertThat(userService.findUserById(userOne.getId())).isEqualTo(userOne);
//        assertThat(userService.findUserById(userTwo.getId())).isNotEqualTo(userTwo);
//    }

    @Test
    void findAll() {
    }



    @Test
    void delete() {
    }

    @Test
    void update() {
    }

    @Test
    void findUserById() {
    }

    @Test
    void addFriend() {
    }

    @Test
    void removeFriend() {
    }

    @Test
    void getFriends() {
    }

    @Test
    void commonFriends() {
    }
}