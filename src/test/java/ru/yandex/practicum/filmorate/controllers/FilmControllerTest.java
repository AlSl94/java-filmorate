package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /*Film expectedFilm = new Film(1L, "Diehard", "About bald guy in NYcity",
            1.20, LocalDate.of(1988, 7, 10), new HashSet<>());
    Film expectedFilm2 = new Film(2L, "Diehard 2", "About bald guy somewhere else",
            1.30, LocalDate.of(1990, 7, 10), new HashSet<>());

    @Test
    void findAll() throws Exception {

        mockMvc.perform(post("/films")
                .content(objectMapper.writeValueAsString(expectedFilm)));
        mockMvc.perform(post("/films")
                .content(objectMapper.writeValueAsString(expectedFilm2)));
    }

    @Test
    void add() throws Exception {

                String responseJson = mockMvc.perform(
                post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedFilm))
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expectedFilm, objectMapper.readValue(responseJson, Film.class));
    }*/

    @Test
    void delete() {
    }

    @Test
    void update() {
    }

    @Test
    void getFilm() {
    }

    @Test
    void likeFilm() {
    }

    @Test
    void unlikeFilm() {
    }

    @Test
    void filmsByLikesDefault() {
    }
}