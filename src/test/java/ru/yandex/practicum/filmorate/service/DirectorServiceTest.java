package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorServiceTest {

    private final DirectorService directorService;

    @Test
    void getAllDirectorsTest() {
        directorService.create(directors().get(0));
        assertThat(directorService.getAllDirectors().size()).isEqualTo(1);
        directorService.create(directors().get(1));
        assertThat(directorService.getAllDirectors().size()).isEqualTo(2);
    }

    @Test
    void findDirectorByIdTest() {
        directorService.create(directors().get(0));

        Director director = directorService.findDirectorById(1);
        assertThat(director.getId()).isEqualTo(1);
        assertThat(director.getName()).isEqualTo("Вася Спилберг");
    }

    @Test
    void createTest() {
        Director createdDirector1 = directorService.create(directors().get(0));
        assertThat(createdDirector1.getName()).isEqualTo(directors().get(0).getName());


        assertThat(directors().get(1).getId()).isEqualTo(999);
        Director createDirector2 = directorService.create(directors().get(1));
        assertThat(createDirector2.getId()).isEqualTo(2);
    }

    @Test
    void updateTest() {
        Director createdDirector = directorService.create(directors().get(0));

        assertThat(createdDirector.getName()).isEqualTo("Вася Спилберг");
        assertThat(createdDirector.getId()).isEqualTo(1);

        Director updatedDirector = directorService.update(new Director(1, "Галя Турман"));

        assertThat(updatedDirector.getId()).isEqualTo(1);
        assertThat(updatedDirector.getName()).isEqualTo("Галя Турман");
    }

    @Test
    void deleteTest() {
        directorService.create(directors().get(0));
        directorService.create(directors().get(1));

        assertThat(directorService.getAllDirectors().size()).isEqualTo(2);

        directorService.delete(1);
        assertThat(directorService.getAllDirectors().size()).isEqualTo(1);
    }

    private List<Director> directors() {
        List<Director> directors = new ArrayList<>();
        directors.add(new Director(1, "Вася Спилберг"));
        directors.add(new Director(999, "Петя Шварцнегер"));
        return directors;
    }
}