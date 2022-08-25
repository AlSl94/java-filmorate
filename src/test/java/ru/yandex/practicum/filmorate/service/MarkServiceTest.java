package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mark.MarkDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MarkServiceTest {

    private final MarkService markService;

    private final MarkDbStorage markDbStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final DirectorService directorService;

    @Test
    void scoreFilmTest() {
        Film dieHard = filmService.add(films().get(0));

        User userOne = userService.create(users().get(0));
        User userTwo = userService.create(users().get(1));

        markService.scoreFilm(dieHard.getId(), userOne.getId(), 10);
        markService.scoreFilm(dieHard.getId(), userTwo.getId(), 5);

        Double filmScore = markDbStorage.averageFilmRating(dieHard.getId());

        assertThat(filmScore).isEqualTo(7.5);
    }

    @Test
    void removeFilmScoreTest() {
        Film dieHard = filmService.add(films().get(0));

        User userOne = userService.create(users().get(0));
        User userTwo = userService.create(users().get(1));

        markService.scoreFilm(dieHard.getId(), userOne.getId(), 10);
        markService.scoreFilm(dieHard.getId(), userTwo.getId(), 4);

        Double filmScore = markDbStorage.averageFilmRating(dieHard.getId());
        assertThat(filmScore).isEqualTo(7);

        markService.removeFilmScore(dieHard.getId(), userTwo.getId());
        filmScore = markDbStorage.averageFilmRating(dieHard.getId());
        assertThat(filmScore).isEqualTo(10);
    }

    @Test
    void getBestFilmsByGenresTest() {
        Film dieHard = filmService.add(films().get(0));
        Film dieHard2 = filmService.add(films().get(1));

        User userOne = userService.create(users().get(0));
        User userTwo = userService.create(users().get(1));
        User userThree = userService.create(users().get(2));

        markService.scoreFilm(dieHard.getId(), userOne.getId(), 8);
        markService.scoreFilm(dieHard.getId(), userTwo.getId(), 7);
        markService.scoreFilm(dieHard2.getId(), userOne.getId(), 8);
        markService.scoreFilm(dieHard2.getId(), userTwo.getId(), 8);

        List<Film> bestFilmsByGenre = (List<Film>) markService.getBestFilms(2, 5L, null);

        assertThat(bestFilmsByGenre).hasSize(2);
        assertThat(bestFilmsByGenre.get(0)).isEqualTo(dieHard2);

        markService.scoreFilm(dieHard.getId(), userThree.getId(), 10);

        bestFilmsByGenre = (List<Film>) markService.getBestFilms(2, 5L, null);

        assertThat(bestFilmsByGenre.get(0)).isEqualTo(dieHard);
    }

    @Test
    void getBestFilmsByYearTest() {
        Film dieHard = filmService.add(films().get(0));
        Film dieHard2 = filmService.add(films().get(1));

        User userOne = userService.create(users().get(0));
        User userTwo = userService.create(users().get(1));

        markService.scoreFilm(dieHard.getId(), userOne.getId(), 8);
        markService.scoreFilm(dieHard.getId(), userTwo.getId(), 7);

        List<Film> bestFilmsByGenre = (List<Film>) markService.getBestFilms(2, null, 1988);

        assertThat(bestFilmsByGenre).hasSize(1);
        assertThat(bestFilmsByGenre.get(0)).isEqualTo(dieHard);

        bestFilmsByGenre = (List<Film>) markService.getBestFilms(2, null, 1990);

        assertThat(bestFilmsByGenre.get(0)).isEqualTo(dieHard2);
    }

    @Test
    void getBestFilmsByGenresAndYearTest() {
        Film dieHard = filmService.add(films().get(0));
        Film dieHard2 = filmService.add(films().get(1));

        User userOne = userService.create(users().get(0));
        User userTwo = userService.create(users().get(1));

        markService.scoreFilm(dieHard.getId(), userOne.getId(), 8);
        markService.scoreFilm(dieHard.getId(), userTwo.getId(), 7);
        markService.scoreFilm(dieHard2.getId(), userOne.getId(), 8);
        markService.scoreFilm(dieHard2.getId(), userTwo.getId(), 8);

        markService.scoreFilm(dieHard.getId(), userOne.getId(), 8);
        markService.scoreFilm(dieHard.getId(), userTwo.getId(), 7);

        List<Film> bestFilmsByGenre = (List<Film>) markService.getBestFilms(2, 5L, 1988);

        assertThat(bestFilmsByGenre).hasSize(1);
        assertThat(bestFilmsByGenre.get(0)).isEqualTo(dieHard);

        bestFilmsByGenre = (List<Film>) markService.getBestFilms(2, 5L, 1990);

        assertThat(bestFilmsByGenre.get(0)).isEqualTo(dieHard2);
    }

    private List<User> users() {
        List<User> users = new ArrayList<>();
        users.add(new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10)));
        users.add(new User(null, "testTwo@mail.ru", "TestNameTwo", "TestLoginTwo",
                LocalDate.of(2000, 2, 22)));
        users.add(new User(null, "testThree@mail.ru", "TestNameThree", "TestLoginThree",
                LocalDate.of(1950, 2, 22)));
        return users;
    }

    private List<Film> films() {
        List<Film> films = new ArrayList<>();
        directorService.create(Director.builder().id(1).name("Bob").build());
        directorService.create(Director.builder().id(2).name("Tom").build());

        films.add(new Film(null, "Крепкий орешек", "Фильм о лысом парне",
                LocalDate.of(1988, 7, 12), mpaService.getMpaById(4), 2.13,
                Collections.singletonList(directorService.findDirectorById(1)),
                List.of(genreService.getGenreById(2), genreService.getGenreById(5))));
        films.add(new Film(null, "Крепкий орешек 2", "Фильм о лысом парне 2",
                LocalDate.of(1990, 7, 2), mpaService.getMpaById(5), 2.04,
                Collections.singletonList(directorService.findDirectorById(2)),
                List.of(genreService.getGenreById(3), genreService.getGenreById(5))));
        return films;
    }
}