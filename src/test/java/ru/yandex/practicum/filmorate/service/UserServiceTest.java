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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {

    private final UserService userService;

    @Test
    void findAllTest() {
        User userTemplateOne = new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10));
        User userTemplateTwo = new User(null, "testTwo@mail.ru", "TestNameTwo", "TestLoginTwo",
                LocalDate.of(2000, 2, 22));
        User userOne = userService.create(userTemplateOne);
        User userTwo = userService.create(userTemplateTwo);

        ArrayList<User> users = (ArrayList<User>) userService.findAll();

        assertThat(users.size()).isEqualTo(2);
        assertThat(userOne).isEqualTo(users.get(0));
        assertThat(userTwo).isEqualTo(users.get(1));

    }

    @Test
    void createUserTest() {
        User userTemplateOne = new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10));
        User userTemplateTwo = new User(null, "testTwo@mail.ru", "TestNameTwo", "TestLoginTwo",
                LocalDate.of(2000, 2, 22));

        User userOne = userService.create(userTemplateOne);
        userService.create(userTemplateTwo);

        assertThat(userOne.getId()).isEqualTo(1);
        assertThat(userService.findAll().size()).isEqualTo(2);
    }

    @Test
    void deleteTest() {
        User userTemplateOne = new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10));
        User userTemplateTwo = new User(null, "testTwo@mail.ru", "TestNameTwo", "TestLoginTwo",
                LocalDate.of(2000, 2, 22));

        userService.create(userTemplateOne);
        User userTwo = userService.create(userTemplateTwo);

        assertThat(userService.findAll().size()).isEqualTo(2);
        assertThat(userService.findUserById(2L).getName()).isEqualTo(userTwo.getName());

        userService.delete(2L);

        assertThat(userService.findAll().size()).isEqualTo(1);
    }

    @Test
    void deleteWrongIdThrowsExceptionTest() {
        assertThatThrownBy(() -> userService.delete(-1L)).isInstanceOf(WrongParameterException.class);
    }

    @Test
    void updateTest() {
        User userTemplateOne = new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10));
        User userTemplateTwo = new User(1L, "testTwo@mail.ru", "TestNameTwo", "TestLoginTwo",
                LocalDate.of(2000, 2, 22));

        User userOne = userService.create(userTemplateOne);
        assertThat(userOne.getEmail()).isEqualTo(userTemplateOne.getEmail());
        userService.update(userTemplateTwo);
        User updatedUser = userService.findUserById(1L);

        assertThat(updatedUser.getEmail()).isNotEqualTo(userTemplateOne.getEmail());
        assertThat(updatedUser.getEmail()).isEqualTo(userTemplateTwo.getEmail());
    }

    @Test
    void updateWrongIdThrowsExceptionTest() {
        User userTemplateOne = new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10));
        assertThatThrownBy(() -> userService.update(userTemplateOne)).isInstanceOf(WrongParameterException.class);
    }

    @Test
    void findUserById() {
        User userTemplateOne = new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10));
        User userOne = userService.create(userTemplateOne);

        User userFoundByIdOne = userService.findUserById(1L);

        assertThat(userOne).isEqualTo(userFoundByIdOne);
    }

    @Test
    void findUserByWrongIdThrowsExceptionTest() {
        assertThatThrownBy(() -> userService.findUserById(-1L)).isInstanceOf(WrongParameterException.class);
    }


}
