package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mark.MarkStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTest {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final MarkStorage markStorage;
    private final DirectorStorage directorStorage;

    @Test
    @Order(1)
    public void getRecommendationsTest_standard() {
        createUsers(6);
        createFilms(11);
        createMarks();

        List<Long> expectedFilmsIds = List.of(5L, 8L, 10L, 6L, 7L, 9L);
        Collection<Film> expected = new ArrayList<>();
        for (Long filmId : expectedFilmsIds) {
            expected.add(filmStorage.findFilmById(filmId));
        }

        Collection<Film> real = userStorage.getRecommendations(1L);
        Assertions.assertEquals(expected, real);
    }

    @Test
    public void getRecommendationsTest_userNotExist_thenEmptyList() {
        Collection<Film> expected = Collections.emptyList();
        Collection<Film> real = new ArrayList<>(userStorage.getRecommendations(777L));

        Assertions.assertEquals(expected, real);
    }

    @Test
    public void getRecommendationsTest_userHasNoSimilarUsers_thenEmptyList() {
        Collection<Film> expected = Collections.emptyList();
        Collection<Film> real = userStorage.getRecommendations(5L);

        Assertions.assertEquals(expected, real);
    }

    @Test
    public void getRecommendationsTest_userDoesNotMarkAnyFilm() {
        Collection<Film> expected = Collections.emptyList();
        Collection<Film> real = userStorage.getRecommendations(6L);

        Assertions.assertEquals(expected, real);
    }

    private void createUsers(int count) {
        for (int i = 0; i < count; i++) {
            String email = String.format("user%s@mail.ru", i);
            String name = String.format("user%s", i);
            String login = String.format("user%s", i);
            userStorage.create(new User(null, email, name, login, LocalDate.of(2000, 1, 1)));
        }
    }

    private void createFilms(int count) {
        directorStorage.create(new Director(1, "Гай ричи"));
        for (int i = 0; i < count; i++) {
            String name = String.format("film%s", i);
            String description = String.format("desc%s", i);
            filmStorage.add(new Film(null, name, description, LocalDate.of(1990, 1, 1),
                    new Mpa((short) 1, ""), 24, List.of(new Director(1, "director")), List.of(new Genre(1, null))));
        }
    }

    private void createMarks() {
        markStorage.scoreFilm(1L, 1L, 8);
        markStorage.scoreFilm(1L, 2L, 6);
        markStorage.scoreFilm(1L, 3L, 9);
        markStorage.scoreFilm(1L, 4L, 8);

        markStorage.scoreFilm(2L, 1L, 7);
        markStorage.scoreFilm(2L, 3L, 7);
        markStorage.scoreFilm(2L, 4L, 10);

        markStorage.scoreFilm(3L, 1L, 9);
        markStorage.scoreFilm(3L, 2L, 6);
        markStorage.scoreFilm(3L, 4L, 7);

        markStorage.scoreFilm(4L, 1L, 5);
        markStorage.scoreFilm(4L, 2L, 8);
        markStorage.scoreFilm(4L, 3L, 4);
        markStorage.scoreFilm(4L, 4L, 5);

        markStorage.scoreFilm(5L, 2L, 8);
        markStorage.scoreFilm(5L, 3L, 9);
        markStorage.scoreFilm(5L, 4L, 8);

        markStorage.scoreFilm(6L, 3L, 6);
        markStorage.scoreFilm(6L, 4L, 7);

        markStorage.scoreFilm(7L, 3L, 5);
        markStorage.scoreFilm(7L, 4L, 3);

        markStorage.scoreFilm(8L, 3L, 9);
        markStorage.scoreFilm(8L, 4L, 10);

        markStorage.scoreFilm(9L, 2L, 4);
        markStorage.scoreFilm(10L, 4L, 8);
        markStorage.scoreFilm(11L, 5L, 10);
    }
}
