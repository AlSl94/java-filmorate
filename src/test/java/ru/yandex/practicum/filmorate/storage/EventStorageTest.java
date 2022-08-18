package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.*;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventStorageTest {

    private final UserService userService;
    private final FilmService filmService;
    private final DirectorService directorService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final FriendService friendService;
    private final LikeService likeService;
    private final EventStorage eventStorage;

    @Test
    void getFeedByUserIdTest() {

        // создаём и добавляем в БД режиссёра №1
        Director directorBob = Director.builder()
                .id(1)
                .name("Bob")
                .build();
        directorService.create(directorBob);

        // создаём и добавляем в БД фильм №1
        Film firstFilm = Film.builder()
                .id(1L)
                .name("Крепкий орешек")
                .description("Фильм о лысом парне")
                .releaseDate(LocalDate.of(1988, 7, 12))
                .duration(2.13)
                .mpa(mpaService.getMpaById(4))
                .directors(new ArrayList<>(List.of(directorService.findDirectorById(1))))
                .genres(new ArrayList<>(List.of(genreService.getGenreById(2), genreService.getGenreById(5))))
                .build();
        filmService.add(firstFilm);

        // создаём и добавляем в БД режиссёра №2
        Director directorTom = Director.builder()
                .id(2)
                .name("Tom")
                .build();
        directorService.create(directorTom);

        // создаём и добавляем в БД фильм №2
        Film secondFilm = Film.builder()
                .id(2L)
                .name("Крепкий орешек 2")
                .description("Фильм о лысом парне 2")
                .releaseDate(LocalDate.of(1990, 7, 2))
                .duration(2.04)
                .mpa(mpaService.getMpaById(5))
                .directors(new ArrayList<>(List.of(directorService.findDirectorById(2))))
                .genres(new ArrayList<>(List.of(genreService.getGenreById(3), genreService.getGenreById(5))))
                .build();
        filmService.add(secondFilm);

        // создаём и добавляем в БД пользователя №1
        User firstUser = User.builder()
                .id(1L)
                .login("TestLoginOne")
                .email("testOne@mail.ru")
                .name("TestNameOne")
                .birthday(LocalDate.of(1994, 2, 10))
                .build();
        userService.create(firstUser);

        // создаём и добавляем в БД пользователя №2
        User secondUser = User.builder()
                .id(1L)
                .login("TestLoginTwo")
                .email("testTwo@mail.ru")
                .name("TestNameTwo")
                .birthday(LocalDate.of(2000, 2, 22))
                .build();
        userService.create(secondUser);

        // пользователь №2 добавляет в друзья пользователя №1
        friendService.addFriend(2L, 1L);

        // пользователь №1 поставил лайк фильму №1
        likeService.likeFilm(1L, 1L);

        // пользователь №2 поставил лайк фильму №2
        likeService.likeFilm(2L, 2L);

        // пользователь №1 добавляет в друзья пользователя №2
        friendService.addFriend(1L, 2L);

        // получаем ленту событий по пользователю №2:
        // - всего 2 события
        final int expectedEventsQty = 2;

        // первое событие - добавил в друзья пользователя №1
        final long expectedFriendId = 1L;

        // - последнее (свежее) событие - лайк фильму №2
        final long expectedFilmId = 2L;

        // получаем коллекцию событий, превращаем в ленту (список) событий
        List<Event> secondUserEventsList = new ArrayList<>(eventStorage.getFeedByUserId(2L));

        // ***  ПОЯСНЕНИЕ ***
        // События сохраняется по мере их возникновения, т.е. самые "свежие" события будут В КОНЦЕ (ВНИЗУ) списка.
        // Запрос ленты событий требуется (по тестам Postman) упорядоченным по id событий, ПО ВОЗРАСТАНИЮ.

        // проверяем размер ленты событий, ожидаем 2
        assertThat(secondUserEventsList)
                .asList()
                .hasSize(expectedEventsQty);

        // проверяем "старое" событие - добавление в друзья пользователя с id = 1
        final long actualFriendId = secondUserEventsList.get(0).getEntityId(); // первый пункт в списке
        assertThat(actualFriendId)
                .isEqualTo(expectedFriendId);

        // проверяем самое "свежее" событие - лайк фильму с id = 2
        final long actualFilmId = secondUserEventsList.get(1).getEntityId(); // второй (последний) пункт в списке
        assertThat(actualFilmId)
                .isEqualTo(expectedFilmId);
    }
}
