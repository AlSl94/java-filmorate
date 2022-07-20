package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceTest {

    private final FilmService filmService;
    private final MpaService mpaService;

    @Test
    void findAllTest() {
        Film filmOne = new Film(null, "Крепкий орешек", "Фильм о лысом парне",
                LocalDate.of(1988, 7, 12),
                mpaService.getMpaById(4), 2.13, null, null);
        Film filmTwo = new Film(null, "Крепкий орешек 2", "Фильм о лысом парне 2",
                LocalDate.of(1990, 7, 2),
                mpaService.getMpaById(5), 2.04, null, null);

        filmService.add(filmOne);
        filmService.add(filmTwo);

        List<Film> allFilms = (List<Film>) filmService.findAll();

        assertThat(allFilms.size()).isEqualTo(2);


    }

    @Test
    void findFilmById() {
        Film filmOne = new Film(null, "Крепкий орешек", "Фильм о лысом парне",
                LocalDate.of(1988, 7, 12),
                mpaService.getMpaById(4), 2.13, null, null);
        Film filmTwo = new Film(null, "Крепкий орешек 2", "Фильм о лысом парне 2",
                LocalDate.of(1990, 7, 2),
                mpaService.getMpaById(5), 2.04, null, null);

        Film dieHard = filmService.add(filmOne);
        filmService.add(filmTwo);

        filmService.findFilmById(dieHard.getId());

    }

    @Test
    void addTest() {
        Film filmOne = new Film(null, "Крепкий орешек", "Фильм о лысом парне",
                LocalDate.of(1988, 7, 12),
                mpaService.getMpaById(4), 2.13, null, null);
        Film filmTwo = new Film(null, "Крепкий орешек 2", "Фильм о лысом парне 2",
                LocalDate.of(1990, 7, 2),
                mpaService.getMpaById(5), 2.04, null, null);

        Film dieHard = filmService.add(filmOne);
        Film dieHard2 = filmService.add(filmTwo);

        assertThat(dieHard.getName()).isEqualTo(filmOne.getName());
        assertThat(dieHard2.getName()).isEqualTo(filmTwo.getName());

    }

    @Test
    void deleteTest() {
        Film filmOne = new Film(null, "Крепкий орешек", "Фильм о лысом парне",
                LocalDate.of(1988, 7, 12),
                mpaService.getMpaById(4), 2.13, null, null);
        Film filmTwo = new Film(null, "Крепкий орешек 2", "Фильм о лысом парне 2",
                LocalDate.of(1990, 7, 2),
                mpaService.getMpaById(5), 2.04, null, null);

        filmService.add(filmOne);
        filmService.add(filmTwo);

        assertThat(filmService.findAll().size()).isEqualTo(2);

        filmService.delete(filmOne.getId());

        assertThat(filmService.findAll().size()).isEqualTo(1);

    }

    @Test
    void updateTest() {
        Film filmOne = new Film(null, "Крепкий орешек", "Фильм о лысом парне",
                LocalDate.of(1988, 7, 12),
                mpaService.getMpaById(4), 2.13, null, null);
        Film filmTwo = new Film(1L, "Крепкий орешек 2", "Фильм о лысом парне 2",
                LocalDate.of(1990, 7, 2),
                mpaService.getMpaById(5), 2.04, null, null);
        Film dieHard = filmService.add(filmOne);

        assertThat(filmService.findFilmById(dieHard.getId()).getName()).isEqualTo("Крепкий орешек");

        Film updatedDieHard = filmService.update(filmTwo);

        assertThat(filmService.findFilmById(updatedDieHard.getId()).getName()).isEqualTo("Крепкий орешек 2");

    }
}