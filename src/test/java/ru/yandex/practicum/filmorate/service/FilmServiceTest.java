package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceTest {

    private final FilmService filmService;
    private final MpaService mpaService;
    private final DirectorService directorService;
    private final GenreService genreService;
    private final LikeService likeService;

    private final UserService userService;

    @Test
    void findAllTest() {
        filmService.add(films().get(0));
        filmService.add(films().get(1));

        List<Film> allFilms = (List<Film>) filmService.findAll();

        assertThat(allFilms.size()).isEqualTo(2);
        assertThat(allFilms.get(0).getName()).isEqualTo(films().get(0).getName());
    }

    @Test
    void findFilmById() {
        Film dieHard = filmService.add(films().get(0));
        Film dieHard2 = filmService.add(films().get(1));

        Film checkFilm1 = filmService.findFilmById(dieHard.getId());
        Film checkFilm2 = filmService.findFilmById(dieHard2.getId());

        assertThat(dieHard.getName()).isEqualTo(checkFilm1.getName());
        assertThat(dieHard2.getName()).isEqualTo(checkFilm2.getName());
    }

    @Test
    void addTest() {
        Film dieHard = filmService.add(films().get(0));
        Film dieHard2 = filmService.add(films().get(1));

        assertThat(dieHard.getName()).isEqualTo(films().get(0).getName());
        assertThat(dieHard2.getName()).isEqualTo(films().get(1).getName());
    }

    @Test
    void deleteTest() {
        Film dieHard = filmService.add(films().get(0));
        Film dieHard2 = filmService.add(films().get(1));

        assertThat(filmService.findAll().size()).isEqualTo(2);
        assertThat(filmService.findFilmById(1L).getName()).isEqualTo(films().get(0).getName());

        filmService.delete(dieHard.getId());

        assertThatThrownBy(() -> filmService.findFilmById(1L)).isInstanceOf(WrongParameterException.class);
        assertThat(filmService.findAll().size()).isEqualTo(1);
    }

    @Test
    void updateTest() {
        Film updatedFilm = new Film(1L, "Крепкий орешек 2", "Фильм о лысом парне 2",
                LocalDate.of(1990, 7, 2), mpaService.getMpaById(5), 2.04,
                Arrays.asList(new Director(1, "Вася Спилберг")), null);

        Film dieHard = filmService.add(films().get(0));
        assertThat(filmService.findFilmById(dieHard.getId()).getName()).isEqualTo("Крепкий орешек");

        Film updatedDieHard = filmService.update(updatedFilm);
        assertThat(filmService.findFilmById(updatedDieHard.getId()).getName()).isEqualTo("Крепкий орешек 2");
    }

    @Test
    void getFilmsByDirectorByYearTest() {
        Film dieHard = filmService.add(films().get(0));
        Film dieHard2 = filmService.add(films().get(1));

        List<Film> filmsByYear = (List<Film>) filmService.getFilmsByDirector(1, "year");
        assertThat(filmsByYear.get(0)).isEqualTo(dieHard);
        assertThat(filmsByYear.get(1)).isEqualTo(dieHard2);
    }

    @Test
    void getFilmsByDirectorByLikesTest() {
        Film dieHard = filmService.add(films().get(0));
        Film dieHard2 = filmService.add(films().get(1));

        User userOne = userService.create(users().get(0));
        User userTwo = userService.create(users().get(1));

        likeService.likeFilm(dieHard.getId(), userOne.getId());
        likeService.likeFilm(dieHard.getId(), userTwo.getId());
        likeService.likeFilm(dieHard2.getId(), userOne.getId());

        List<Film> filmsByLikes = (List<Film>) filmService.getFilmsByDirector(1, "likes");

        assertThat(filmsByLikes.get(0)).isEqualTo(dieHard);
        assertThat(filmsByLikes.get(1)).isEqualTo(dieHard2);

        likeService.unlikeFilm(dieHard.getId(), userOne.getId());
        likeService.likeFilm(dieHard2.getId(), userTwo.getId());

        List<Film> filmsByLikes2 = (List<Film>) filmService.getFilmsByDirector(1, "likes");

        assertThat(filmsByLikes2.get(0)).isEqualTo(dieHard2);
        assertThat(filmsByLikes2.get(1)).isEqualTo(dieHard);
    }

    @Test
    void commonFilmsTest() {
        // TODO тут просят добавить хотя бы один тест-кейс
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
        directorService.create(new Director(1, "Вася Спилберг"));

        films.add(new Film(null, "Крепкий орешек", "Фильм о лысом парне",
                LocalDate.of(1988, 7, 12), mpaService.getMpaById(4), 2.13,
                Arrays.asList(directorService.findDirectorById(1)),
                List.of(genreService.getGenreById(3), genreService.getGenreById(5))));
        films.add(new Film(null, "Крепкий орешек 2", "Фильм о лысом парне 2",
                LocalDate.of(1990, 7, 2), mpaService.getMpaById(5), 2.04,
                Arrays.asList(directorService.findDirectorById(1)),
                List.of(genreService.getGenreById(3), genreService.getGenreById(5))));
        return films;
    }

}