package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaServiceTest {

    private final MpaService mpaService;

    @Test
    void GetMpaByIdTest() {
        Mpa mpaOne = mpaService.getMpaById(1);
        Mpa mpaTwo = mpaService.getMpaById(5);
        assertThat(mpaOne.getName()).isEqualTo("G");
        assertThat(mpaTwo.getName()).isEqualTo("NC-17");
    }

    @Test
    void allMpaTest() {
        Mpa mpaOne = mpaService.getMpaById(1);
        Collection<Mpa> mpaCollection = mpaService.allMpa();
        assertThat(mpaCollection.size()).isEqualTo(5);
        assertThat(mpaOne).isIn(mpaCollection);
        assertThat(mpaCollection.size()).isNotEqualTo(6);
    }
}