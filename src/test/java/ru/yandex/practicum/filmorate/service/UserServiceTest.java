package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {

    private final UserService userService;

    @Test
    void findAllTest() {
        User userOne = userService.create(users().get(0));
        User userTwo = userService.create(users().get(1));

        ArrayList<User> allUsers = (ArrayList<User>) userService.findAll();

        assertThat(allUsers).hasSize(2);
        assertThat(userOne).isEqualTo(allUsers.get(0));
        assertThat(userTwo).isEqualTo(allUsers.get(1));

    }

    @Test
    void createUserTest() {
        User userOne = userService.create(users().get(0));
        User userTwo = userService.create(users().get(1));

        assertThat(userOne.getId()).isEqualTo(1);
        assertThat(userService.findAll()).hasSize(2);
        assertThat(userTwo.getName()).isEqualTo(users().get(1).getName());
    }

    @Test
    void deleteTest() {
        userService.create(users().get(0));
        User userTwo = userService.create(users().get(1));

        assertThat(userService.findAll()).hasSize(2);
        assertThat(userService.findUserById(2L).getName()).isEqualTo(userTwo.getName());

        userService.delete(2L);

        assertThat(userService.findAll()).hasSize(1);
    }

    @Test
    void deleteWrongIdThrowsExceptionTest() {
        assertThatThrownBy(() -> userService.delete(-1L)).isInstanceOf(WrongParameterException.class);
    }

    @Test
    void updateTest() {
        User userOne = userService.create(users().get(0));
        User userTwo = users().get(1);
        userTwo.setId(1L);

        assertThat(userOne.getEmail()).isEqualTo(users().get(0).getEmail());
        userService.update(userTwo);

        User updatedUser = userService.findUserById(1L);

        assertThat(updatedUser.getEmail()).isNotEqualTo(userOne.getEmail());
        assertThat(updatedUser.getEmail()).isEqualTo(userTwo.getEmail());
    }

    @Test
    void updateWrongIdThrowsExceptionTest() {
        User userOne = users().get(0);
        assertThatThrownBy(() -> userService.update(userOne)).isInstanceOf(WrongParameterException.class);
    }

    @Test
    void findUserById() {
        User userOne = userService.create(users().get(0));

        User userFoundByIdOne = userService.findUserById(1L);

        assertThat(userOne).isEqualTo(userFoundByIdOne);
    }

    @Test
    void findUserByWrongIdThrowsExceptionTest() {
        assertThatThrownBy(() -> userService.findUserById(-1L)).isInstanceOf(WrongParameterException.class);
    }

    private List<User> users() {
        List<User> users = new ArrayList<>();
        users.add(new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10)));
        users.add(new User(null, "testTwo@mail.ru", "TestNameTwo", "TestLoginTwo",
                LocalDate.of(2000, 2, 22)));
        return users;
    }
}