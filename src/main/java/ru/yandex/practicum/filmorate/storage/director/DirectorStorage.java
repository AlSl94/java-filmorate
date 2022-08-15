package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Collection<Director> allDirectors();

    Director findDirectorById(Integer id);

    Director create(Director director);

    Director update(Director director);

    void delete(Integer id);
}
