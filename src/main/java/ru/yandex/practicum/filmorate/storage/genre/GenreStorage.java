package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    Genre genre(Integer id);

    List<Genre> allGenres();

    List<Genre> loadFilmGenre(Long id);
}
