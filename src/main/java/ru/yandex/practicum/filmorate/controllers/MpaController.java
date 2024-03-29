package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(
        value = "/mpa",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping(value = "/{id}")
    public Mpa getMpaById(@PathVariable Integer id) {
        Mpa mpa = mpaService.getMpaById(id);
        log.info("Получен mpa с id: {}", id);
        return mpa;
    }

    @GetMapping
    public Collection<Mpa> allMpa() {
        Collection<Mpa> mpaCollection = mpaService.allMpa();
        log.info("Получен список всех mpa");
        return mpaCollection;
    }
}