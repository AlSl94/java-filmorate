package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendServiceTest {

    private final FriendService friendService;
    private final UserService userService;

    @Test
    void addFriendTest() {
        User userTemplateOne = new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10));
        User userTemplateTwo = new User(null, "testTwo@mail.ru", "TestNameTwo", "TestLoginTwo",
                LocalDate.of(2000, 2, 22));
        User userOne = userService.create(userTemplateOne);
        User userTwo = userService.create(userTemplateTwo);

        friendService.addFriend(userOne.getId(), userTwo.getId());
        friendService.addFriend(userTwo.getId(), userOne.getId());

        List<User> friendsOfUserTwo = (List<User>) friendService.getFriends(userTwo.getId());

        assertThat(userOne).isEqualTo(friendsOfUserTwo.get(0));
    }

    @Test
    void getFriendsTest() {
        User userTemplateOne = new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10));
        User userTemplateTwo = new User(null, "testTwo@mail.ru", "TestNameTwo", "TestLoginTwo",
                LocalDate.of(2000, 2, 22));
        User userTemplateThree = new User(null, "testThree@mail.ru", "TestNameThree",
                "TestLoginThree", LocalDate.of(2004, 1, 11));

        User userOne = userService.create(userTemplateOne);
        User userTwo = userService.create(userTemplateTwo);
        User userThree = userService.create(userTemplateThree);

        friendService.addFriend(userOne.getId(), userTwo.getId());
        friendService.addFriend(userOne.getId(), userThree.getId());

        Collection<User> friends = friendService.getFriends(1L);

        assertThat(userThree).isIn(friends);
        assertThat(userTwo).isIn(friends);
        assertThat(userOne).isNotIn(friends);
    }

    @Test
    void commonFriendsTest() {
        User userTemplateOne = new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10));
        User userTemplateTwo = new User(null, "testTwo@mail.ru", "TestNameTwo", "TestLoginTwo",
                LocalDate.of(2000, 2, 22));
        User userTemplateThree = new User(null, "testThree@mail.ru", "TestNameThree",
                "TestLoginThree", LocalDate.of(2004, 1, 11));
        User userTemplateFour = new User(null, "testFour@mail.ru", "TestNameFour",
                "TestLoginFour", LocalDate.of(1950, 1, 10));

        User userOne = userService.create(userTemplateOne);
        User userTwo = userService.create(userTemplateTwo);
        User userThree = userService.create(userTemplateThree);
        User userFour = userService.create(userTemplateFour);

        friendService.addFriend(userOne.getId(), userThree.getId());
        friendService.addFriend(userOne.getId(), userFour.getId());
        friendService.addFriend(userTwo.getId(), userThree.getId());
        friendService.addFriend(userTwo.getId(), userFour.getId());

        Collection<User> commonFriends = friendService.commonFriends(1L, 2L);

        assertThat(userThree).isIn(commonFriends);
        assertThat(userFour).isIn(commonFriends);
        assertThat(commonFriends.size()).isEqualTo(2);
        assertThat(userOne).isNotIn(commonFriends);
    }
}