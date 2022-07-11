package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    @Getter
    private final Map<Long, Film> films = new HashMap<>();
    private long id = 0;

    /**
     * Возвращает все фильмы
     * @return все фильмы из мапы films
     */
    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    /**
     * Добавляет новый фильм
     * @param film новый фильм
     * @return новый фильм
     */
    @Override
    public Film add(Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        return film;
    }

    /**
     * Обновляет фильм
     * @param film обновленный фильм
     * @return обновленный фильм
     */
    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    /**
     * Получаем конкретный фильм из мапы
     * @param id айди фильма
     * @return фильм, который получили по айди
     */
    @Override
    public Film getFilm(Long id) {
        return films.get(id);
    }
}
