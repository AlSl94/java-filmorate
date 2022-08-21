package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.Objects;

@Service
@Validated
public class GenreService {

    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    /**
     * Метод для получения жанра по id
     * @param id - айди
     * @return - экземпляр класса Genre
     */
    public Genre getGenreById(Integer id) {
        if (allGenres().stream().noneMatch(g -> Objects.equals(g.getId(), id))) {
            throw new WrongParameterException("Неверный id");
        }
        return genreStorage.getGenreById(id);
    }

    /**
     * Метод для получения коллекции со всеми жанрами
     * @return коллекция с жанрами
     */
    public Collection<Genre> allGenres() {
        return genreStorage.allGenres();
    }
}
