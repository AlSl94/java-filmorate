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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LikeServiceTest {

    private final LikeService likeService;
    private final FilmService filmService;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final DirectorService directorService;

    @Test
    void likeFilmTest() {
        Director directorBob = Director.builder().id(1).name("Bob").build();
        directorService.create(directorBob);
        Film filmOne = new Film(null, "Крепкий орешек", "Фильм о лысом парне",
                LocalDate.of(1988, 7, 12),
                mpaService.getMpaById(4), 2.13,
                new ArrayList<>(List.of(directorService.findDirectorById(1))),
                new ArrayList<>(List.of(genreService.getGenreById(2), genreService.getGenreById(5))));

        Director directorTom = Director.builder().id(2).name("Tom").build();
        directorService.create(directorTom);
        Film filmTwo = new Film(null, "Крепкий орешек 2", "Фильм о лысом парне 2",
                LocalDate.of(1990, 7, 2),
                mpaService.getMpaById(5), 2.04,
                new ArrayList<>(List.of(directorService.findDirectorById(2))),
                new ArrayList<>(List.of(genreService.getGenreById(3), genreService.getGenreById(5))));
        Film dieHard = filmService.add(filmOne);
        filmService.add(filmTwo);

        User userTemplateOne = new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10));
        User userTemplateTwo = new User(null, "testTwo@mail.ru", "TestNameTwo", "TestLoginTwo",
                LocalDate.of(2000, 2, 22));
        User userOne = userService.create(userTemplateOne);
        userService.create(userTemplateTwo);

        likeService.likeFilm(dieHard.getId(), userOne.getId());
        List<Film> films = (List<Film>) likeService.getPopularFilms(1, 5L, 1988);
        assertThat(dieHard).isEqualTo(films.get(0));
    }

    @Test
    void unlikeFilmTest() {
        Director directorBob = Director.builder().id(1).name("Bob").build();
        directorService.create(directorBob);
        Film filmOne = new Film(null, "Крепкий орешек", "Фильм о лысом парне",
                LocalDate.of(1988, 7, 12),
                mpaService.getMpaById(4), 2.13,
                new ArrayList<>(List.of(directorService.findDirectorById(1))),
                new ArrayList<>(List.of(genreService.getGenreById(2), genreService.getGenreById(5))));

        Director directorTom = Director.builder().id(2).name("Tom").build();
        directorService.create(directorTom);
        Film filmTwo = new Film(null, "Крепкий орешек 2", "Фильм о лысом парне 2",
                LocalDate.of(1990, 7, 2),
                mpaService.getMpaById(5), 2.04,
                new ArrayList<>(List.of(directorService.findDirectorById(2))),
                new ArrayList<>(List.of(genreService.getGenreById(3), genreService.getGenreById(5))));
        Film dieHard = filmService.add(filmOne);
        Film dieHard2 = filmService.add(filmTwo);

        User userTemplateOne = new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10));
        User userTemplateTwo = new User(null, "testTwo@mail.ru", "TestNameTwo", "TestLoginTwo",
                LocalDate.of(2000, 2, 22));
        User userTemplateThree = new User(null, "testThree@mail.ru",
                "TestNameThree", "TestLoginThree", LocalDate.of(1950, 2, 22));
        User userOne = userService.create(userTemplateOne);
        User userTwo = userService.create(userTemplateTwo);
        User userThree = userService.create(userTemplateThree);

        likeService.likeFilm(dieHard.getId(), userOne.getId());
        likeService.likeFilm(dieHard.getId(), userTwo.getId());
        likeService.likeFilm(dieHard.getId(), userThree.getId());
        likeService.likeFilm(dieHard2.getId(), userOne.getId());
        likeService.likeFilm(dieHard2.getId(), userTwo.getId());

        List<Film> films = (List<Film>) likeService.getPopularFilms(1, 5L, 1988);
        assertThat(dieHard).isEqualTo(films.get(0));

        likeService.unlikeFilm(dieHard.getId(), userOne.getId());

        List<Film> films2 = (List<Film>) likeService.getPopularFilms(1, 2L, 1988);

        assertThat(dieHard).isEqualTo(films2.get(0));
    }

    @Test
    void getPopularFilmsTest() {
        Director directorBob = Director.builder().id(1).name("Bob").build();
        directorService.create(directorBob);
        Film filmOne = new Film(null, "Крепкий орешек", "Фильм о лысом парне",
                LocalDate.of(1988, 7, 12),
                mpaService.getMpaById(4), 2.13,
                new ArrayList<>(List.of(directorService.findDirectorById(1))),
                new ArrayList<>(List.of(genreService.getGenreById(2), genreService.getGenreById(5))));

        Director directorTom = Director.builder().id(2).name("Tom").build();
        directorService.create(directorTom);
        Film filmTwo = new Film(null, "Крепкий орешек 2", "Фильм о лысом парне 2",
                LocalDate.of(1990, 7, 2),
                mpaService.getMpaById(5), 2.04,
                new ArrayList<>(List.of(directorService.findDirectorById(2))),
                new ArrayList<>(List.of(genreService.getGenreById(3), genreService.getGenreById(5))));
        Film dieHard = filmService.add(filmOne);
        Film dieHard2 = filmService.add(filmTwo);

        User userTemplateOne = new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10));
        User userTemplateTwo = new User(null, "testTwo@mail.ru", "TestNameTwo", "TestLoginTwo",
                LocalDate.of(2000, 2, 22));
        User userTemplateThree = new User(null, "testThree@mail.ru",
                "TestNameThree", "TestLoginThree", LocalDate.of(1950, 2, 22));
        User userOne = userService.create(userTemplateOne);
        User userTwo = userService.create(userTemplateTwo);
        User userThree = userService.create(userTemplateThree);

        likeService.likeFilm(dieHard.getId(), userOne.getId());
        likeService.likeFilm(dieHard.getId(), userTwo.getId());
        likeService.likeFilm(dieHard.getId(), userThree.getId());
        likeService.likeFilm(dieHard2.getId(), userOne.getId());
        likeService.likeFilm(dieHard2.getId(), userTwo.getId());

        List<Film> popularFilms = (List<Film>) likeService.getPopularFilms(2, 5L, null);

        assertThat(dieHard).isIn(popularFilms);
        assertThat(dieHard2).isIn(popularFilms);
        assertThat(dieHard).isEqualTo(popularFilms.get(0));
        assertThat(dieHard2).isEqualTo(popularFilms.get(1));
    }
}