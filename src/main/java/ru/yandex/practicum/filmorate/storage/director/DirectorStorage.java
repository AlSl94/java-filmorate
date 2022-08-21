package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;

public interface DirectorStorage {
    Collection<Director> getAllDirectors();

    Director findDirectorById(Integer id);

    Director create(Director director);

    Director update(Director director);

    void delete(Integer id);

    List<Director> directorsByFilm(Long id);
}
