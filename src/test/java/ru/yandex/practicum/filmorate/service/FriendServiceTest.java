package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendServiceTest {

    private final FriendService friendService;
    private final UserService userService;

    @Test
    void addFriendTest() {
        User userOne = userService.create(users().get(0));
        User userTwo = userService.create(users().get(1));

        friendService.addFriend(userOne.getId(), userTwo.getId());
        friendService.addFriend(userTwo.getId(), userOne.getId());

        List<User> friendsOfUserTwo = (List<User>) friendService.getFriends(userTwo.getId());

        assertThat(userOne).isEqualTo(friendsOfUserTwo.get(0));
    }

    @Test
    void removeFriendTest() {
        User userOne = userService.create(users().get(0));
        User userTwo = userService.create(users().get(1));
        User userThree = userService.create(users().get(2));
        friendService.addFriend(userOne.getId(), userTwo.getId());
        friendService.addFriend(userOne.getId(), userThree.getId());

        List<User> friends = (List<User>) friendService.getFriends(1L);
        assertThat(friends).hasSize(2);
        assertThat(friends.get(1)).isEqualTo(userThree);

        friendService.removeFriend(1L, 3L);

        List<User> friendsAfterDelete = (List<User>) friendService.getFriends(1L);
        assertThat(friendsAfterDelete)
                .hasSize(1)
                .doesNotContain(userThree);
    }

    @Test
    void getFriendsTest() {
        User userOne = userService.create(users().get(0));
        User userTwo = userService.create(users().get(1));
        User userThree = userService.create(users().get(2));

        friendService.addFriend(userOne.getId(), userTwo.getId());
        friendService.addFriend(userOne.getId(), userThree.getId());

        Collection<User> friends = friendService.getFriends(1L);

        assertThat(userThree).isIn(friends);
        assertThat(userTwo).isIn(friends);
        assertThat(userOne).isNotIn(friends);
    }

    @Test
    void commonFriendsTest() {
        User userOne = userService.create(users().get(0));
        User userTwo = userService.create(users().get(1));
        User userThree = userService.create(users().get(2));
        User userFour = userService.create(users().get(3));

        friendService.addFriend(userOne.getId(), userThree.getId());
        friendService.addFriend(userOne.getId(), userFour.getId());
        friendService.addFriend(userTwo.getId(), userThree.getId());
        friendService.addFriend(userTwo.getId(), userFour.getId());

        Collection<User> commonFriends = friendService.commonFriends(1L, 2L);

        assertThat(userThree).isIn(commonFriends);
        assertThat(userFour).isIn(commonFriends);
        assertThat(commonFriends).hasSize(2);
        assertThat(userOne).isNotIn(commonFriends);
    }

    private List<User> users() {
        List<User> users = new ArrayList<>();
        users.add(new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10)));
        users.add(new User(null, "testTwo@mail.ru", "TestNameTwo", "TestLoginTwo",
                LocalDate.of(2000, 2, 22)));
        users.add(new User(null, "testThree@mail.ru", "TestNameThree", "TestLoginThree",
                LocalDate.of(1950, 2, 22)));
        users.add(new User(null, "testFour@mail.ru", "TestNameFour",
                "TestLoginFour", LocalDate.of(1950, 1, 10)));
        return users;
    }
}