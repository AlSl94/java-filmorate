package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreServiceTest {

    private final GenreService genreService;

    @Test
    void getGenreByIdTest() {
        Genre genreOne = genreService.getGenreById(1);
        Genre genreTwo = genreService.getGenreById(6);

        assertThat(genreOne.getName()).isEqualTo("Комедия");
        assertThat(genreTwo.getName()).isEqualTo("Вестерн");
    }

    @Test
    void allGenresTest() {
        Genre genreOne = genreService.getGenreById(1);

        Collection<Genre> genres = genreService.allGenres();

        assertThat(genreOne).isIn(genres);
        assertThat(genres).hasSize(6);
    }
}