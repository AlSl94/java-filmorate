package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(
        value = "/directors",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public Collection<Director> allDirectors() {
        Collection<Director> directors = directorService.allDirectors();
        log.info("Получены все режиссеры");
        return directors;
    }

    @GetMapping(value = "/{id}")
    public Director findDirectorById(@PathVariable Integer id) {
        Director director = directorService.findDirectorById(id);
        log.info("Получен режиссер {}", director.getName());
        return director;
    }

    @PostMapping
    public Director create(@RequestBody Director directorDto) {
        Director director = directorService.create(directorDto);
        log.info("Создан режиссер {}", director);
        return director;
    }

    @PutMapping
    public Director update(@RequestBody Director directorDto) {
        Director director = directorService.update(directorDto);
        log.info("Обновлен режиссер {}", director);
        return director;
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Integer id) {
        directorService.delete(id);
        log.info("Режиссер с id {} удален", id);
    }

}
